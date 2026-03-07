(function () {
  function loadTab(tab) {
    var url = tab.getAttribute("data-url");
    var target = tab.getAttribute("data-target");
    if (!url || !target) return;
    htmx.ajax("GET", url, { target: target, swap: "innerHTML" });
  }

  document.addEventListener("click", function (e) {
    var tab = e.target.closest(".skyui-tab");
    if (!tab) return;
    var bar = tab.closest(".skyui-tab-bar");
    if (!bar) return;
    bar.querySelectorAll(".skyui-tab").forEach(function (t) {
      t.classList.remove("active");
    });
    tab.classList.add("active");
    loadTab(tab);
  });

  function init() {
    document.querySelectorAll(".skyui-tab[data-autoload]").forEach(function (tab) {
      loadTab(tab);
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
