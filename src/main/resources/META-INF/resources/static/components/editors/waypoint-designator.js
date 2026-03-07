(function () {
  function init() {
    document.addEventListener('input', (event) => {
      if (event.target.matches('.waypoint-designator input')) {
        event.target.value = event.target.value.toUpperCase();
      }
    });
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
