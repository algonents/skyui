function setup() {
  setupResizeHandles();

  document.querySelectorAll(".skyui-datagrid tbody").forEach(tbody => {
    if (tbody.dataset.delegated) return;
    tbody.dataset.delegated = "true";
    tbody.addEventListener("click", (e) => {
      const row = e.target.closest("tr");
      if (!row || e.target.closest("button, input, a")) return;
      tbody.querySelectorAll("tr.selected").forEach(r => r.classList.remove("selected"));
      row.classList.add("selected");
      tbody.dataset.selectedRow = row.id;
    });
  });

  document.querySelectorAll(".skyui-datagrid .save-btn").forEach(btn => {
    if (btn.dataset.validateInit) return;
    btn.dataset.validateInit = "true";
    btn.addEventListener("htmx:configRequest", (e) => {
      const row = btn.closest("tr");
      const inputs = row.querySelectorAll("input, select, textarea");
      for (const input of inputs) {
        if (!input.reportValidity()) {
          e.preventDefault();
          return;
        }
      }
    });
  });
}

function setupResizeHandles() {
  document.querySelectorAll(".skyui-datagrid th .col-resize-handle").forEach(handle => {
    if (handle.dataset.resizeInit) return;
    handle.dataset.resizeInit = "true";
    handle.addEventListener("mousedown", initResize);
  });
}

function initResize(e) {
  e.preventDefault();
  const th = e.target.closest("th");
  const table = th.closest("table");
  const index = Array.from(th.parentElement.children).indexOf(th);
  const col = table.querySelector("colgroup").children[index];

  const startX = e.clientX;
  const startWidth = th.offsetWidth;

  function onMouseMove(e) {
    const newWidth = Math.max(40, startWidth + (e.clientX - startX));
    col.style.width = newWidth + "px";
  }

  function onMouseUp() {
    document.removeEventListener("mousemove", onMouseMove);
    document.removeEventListener("mouseup", onMouseUp);
    document.body.style.cursor = "";
    document.body.style.userSelect = "";
  }

  document.addEventListener("mousemove", onMouseMove);
  document.addEventListener("mouseup", onMouseUp);
  document.body.style.cursor = "col-resize";
  document.body.style.userSelect = "none";
}

(function () {
  function init() {
    setup();
    document.body.addEventListener("htmx:afterSettle", () => setup());
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
