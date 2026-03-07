const TILE_URLS = {
   light: 'https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_nolabels/{z}/{x}/{y}.png',
   dark: 'https://cartodb-basemaps-{s}.global.ssl.fastly.net/dark_nolabels/{z}/{x}/{y}.png'
 };

class MapDesigner extends HTMLElement {
   connectedCallback() {
     this.style.width = this.style.width || '500px';
     this.style.height = this.style.height || '500px';

     const lat = parseFloat(this.getAttribute('lat')) || 0;
     const lng = parseFloat(this.getAttribute('lng')) || 0;
     const zoom = parseInt(this.getAttribute('zoom')) || 10;

     const theme = this.getCurrentTheme();
     this.initLeaflet(lat, lng, zoom, theme);

     const themeObserver = new MutationObserver(() => {
       const newTheme = this.getCurrentTheme();
       if (newTheme !== this._theme) {
         this._theme = newTheme;
         this.updateTileLayer(newTheme);
       }
     });

     themeObserver.observe(document.documentElement, {
       attributes: true,
       attributeFilter: ['data-theme']
     });
     this._themeObserver = themeObserver;

     this._resizeObserver = new ResizeObserver(() => {
         if (this._map) this._map.invalidateSize();
     });
     this._resizeObserver.observe(this);
   }

   disconnectedCallback() {
     this._map.off();
     this._map.remove();
     this._themeObserver?.disconnect();
     this._resizeObserver?.disconnect();
   }

   getCurrentTheme() {
     return document.documentElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
   }

   initLeaflet(lat, lng, zoom, theme) {
     this._map = L.map(this).setView([lat, lng], zoom);
     var marker = L.marker([lat, lng]).addTo(this._map);

     this._tileLayer = L.tileLayer(TILE_URLS[theme], {
       attribution: '&copy; <a href="https://carto.com/">CARTO</a>',
       maxZoom: 19
     }).addTo(this._map);

     this._theme = theme;

     setTimeout(() => this._map.invalidateSize(), 0);
   }

   updateTileLayer(theme) {
     this._tileLayer.setUrl(TILE_URLS[theme]);
   }
}

(function () {
  function init() {
    if (!customElements.get('map-designer')) {
      customElements.define('map-designer', MapDesigner);
    }
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
