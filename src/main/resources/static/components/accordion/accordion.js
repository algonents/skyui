(function () {
  function init() {
    document.querySelectorAll(".accordion-btn").forEach(button => {
      if (button.dataset.accordionInit) return;
      button.dataset.accordionInit = "true";
      const container = button.nextElementSibling;
      if (!container || !container.classList.contains("accordion-container")) return;
      button.addEventListener("click", () => {
        container.classList.toggle("open");
        button.classList.toggle("active");
      });
    });
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
