const PointSearchControl = L.Control.extend({
  options: { position: 'topright' },

  initialize(opts) {
    L.Util.setOptions(this, opts);
    this._searchEndpoint = opts.searchEndpoint;
    this._mapDesignerEl = opts.mapDesignerEl;
    this._addedPoints = new Map();
    this._debounceTimer = null;
    this._currentQuery = '';
    this._currentOffset = 0;
    this._pageSize = 20;
  },

  onAdd(map) {
    const container = L.DomUtil.create('div', 'point-search-control');
    L.DomEvent.disableClickPropagation(container);
    L.DomEvent.disableScrollPropagation(container);

    // Toggle button
    const toggle = L.DomUtil.create('button', 'point-search-toggle', container);
    toggle.type = 'button';
    toggle.innerHTML = '<i class="fa-sharp fa-regular fa-triangle"></i>';
    toggle.title = 'Search designated points';

    // Search panel
    const panel = L.DomUtil.create('div', 'point-search-panel', container);
    panel.style.display = 'none';

    const input = L.DomUtil.create('input', 'point-search-input', panel);
    input.type = 'text';
    input.placeholder = 'Search points...';

    const results = L.DomUtil.create('div', 'point-search-results', panel);

    toggle.addEventListener('click', () => {
      const open = panel.style.display !== 'none';
      panel.style.display = open ? 'none' : 'block';
      if (!open) input.focus();
    });

    input.addEventListener('input', () => {
      clearTimeout(this._debounceTimer);
      const query = input.value.trim();
      if (query.length < 2) {
        results.innerHTML = '';
        return;
      }
      this._currentOffset = 0;
      this._debounceTimer = setTimeout(() => this._search(query, results, map, false), 300);
    });

    this._panel = panel;
    this._input = input;
    this._results = results;
    this._container = container;
    return container;
  },

  onRemove() {
    clearTimeout(this._debounceTimer);
  },

  _search(query, resultsEl, map, append) {
    this._currentQuery = query;
    const gql = {
      query: `query($q: String!, $limit: Int!, $offset: BigInteger!) {
        searchDesignatedPoints(query: $q, limit: $limit, offset: $offset) {
          items { id designator type coordinates { latitude longitude } }
          totalCount
        }
      }`,
      variables: { q: query, limit: this._pageSize, offset: this._currentOffset }
    };

    fetch(this._searchEndpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(gql)
    })
      .then(r => r.json())
      .then(json => {
        const page = json.data?.searchDesignatedPoints;
        if (!page) { resultsEl.innerHTML = ''; return; }
        this._renderResults(page.items, page.totalCount, resultsEl, map, append);
      })
      .catch(() => { resultsEl.innerHTML = '<div class="point-search-error">Search failed</div>'; });
  },

  _renderResults(items, totalCount, resultsEl, map, append) {
    // Remove existing "load more" button
    const existingMore = resultsEl.querySelector('.point-search-more');
    if (existingMore) existingMore.remove();

    if (!append) resultsEl.innerHTML = '';

    if (items.length === 0 && !append) {
      resultsEl.innerHTML = '<div class="point-search-empty">No results</div>';
      return;
    }

    items.forEach(pt => {
      const row = L.DomUtil.create('div', 'point-search-item', resultsEl);
      const added = this._addedPoints.has(pt.id);

      const info = L.DomUtil.create('div', 'point-search-item-info', row);
      const des = L.DomUtil.create('span', 'point-search-designator', info);
      des.textContent = pt.designator;
      if (pt.type) {
        const badge = L.DomUtil.create('span', 'point-search-type', info);
        badge.textContent = pt.type;
      }
      if (pt.coordinates) {
        const coords = L.DomUtil.create('span', 'point-search-coords', info);
        coords.textContent = `${pt.coordinates.latitude.toFixed(4)}, ${pt.coordinates.longitude.toFixed(4)}`;
      }

      if (added) {
        row.classList.add('point-search-item--added');
        const removeBtn = L.DomUtil.create('button', 'point-search-remove', row);
        removeBtn.type = 'button';
        removeBtn.innerHTML = '<i class="fa-solid fa-xmark"></i>';
        removeBtn.title = 'Remove from map';
        removeBtn.addEventListener('click', (e) => {
          e.stopPropagation();
          this._removePoint(pt.id, map);
          row.classList.remove('point-search-item--added');
          removeBtn.remove();
        });
      } else {
        row.addEventListener('click', () => {
          if (pt.coordinates) {
            this._addPoint(pt, map);
            row.classList.add('point-search-item--added');
            const removeBtn = L.DomUtil.create('button', 'point-search-remove', row);
            removeBtn.type = 'button';
            removeBtn.innerHTML = '<i class="fa-solid fa-xmark"></i>';
            removeBtn.title = 'Remove from map';
            removeBtn.addEventListener('click', (e) => {
              e.stopPropagation();
              this._removePoint(pt.id, map);
              row.classList.remove('point-search-item--added');
              removeBtn.remove();
            });
          }
        });
      }
    });

    const loaded = this._currentOffset + items.length;
    if (totalCount > loaded) {
      const more = L.DomUtil.create('div', 'point-search-more', resultsEl);
      more.textContent = `Load more (${totalCount - loaded} remaining)`;
      more.style.cursor = 'pointer';
      more.addEventListener('click', () => {
        this._currentOffset = loaded;
        this._search(this._currentQuery, resultsEl, map, true);
      });
    }
  },

  _addPoint(point, map) {
    const lat = point.coordinates.latitude;
    const lng = point.coordinates.longitude;
    const marker = L.marker([lat, lng]).addTo(map);
    marker.bindPopup(`<b>${point.designator}</b>${point.type ? '<br>' + point.type : ''}`);
    map.panTo([lat, lng]);

    this._addedPoints.set(point.id, marker);

    this._input.value = point.designator;
    this._results.innerHTML = '';
    this._panel.style.display = 'none';

    if (this._mapDesignerEl) {
      this._mapDesignerEl.dispatchEvent(new CustomEvent('mapdesigner:point-added', {
        bubbles: true,
        detail: { id: point.id, designator: point.designator, type: point.type, lat, lng }
      }));
    }
  },

  _removePoint(pointId, map) {
    const marker = this._addedPoints.get(pointId);
    if (marker) {
      map.removeLayer(marker);
      this._addedPoints.delete(pointId);
    }

    if (this._mapDesignerEl) {
      this._mapDesignerEl.dispatchEvent(new CustomEvent('mapdesigner:point-removed', {
        bubbles: true,
        detail: { id: pointId }
      }));
    }
  }
});
