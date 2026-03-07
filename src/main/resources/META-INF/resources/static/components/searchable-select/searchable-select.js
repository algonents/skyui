(function () {
  document.addEventListener("click", function (e) {
    var item = e.target.closest(".skyui-select-item");
    if (item) {
      var dropdown = item.closest(".skyui-searchable-select-dropdown");
      var container = dropdown.closest(".skyui-searchable-select");
      var textInput = container.querySelector(".skyui-searchable-select-input");
      var hiddenInput = container.querySelector("input[type=hidden]");
      hiddenInput.value = item.getAttribute("data-value");
      textInput.value = item.textContent.trim();
      dropdown.innerHTML = "";
      return;
    }

    // Close all dropdowns on outside click
    if (!e.target.closest(".skyui-searchable-select")) {
      document.querySelectorAll(".skyui-searchable-select-dropdown").forEach(function (d) {
        d.innerHTML = "";
      });
    }
  });

  // Re-init for dynamically loaded content (HTMX)
  document.body.addEventListener("htmx:afterSettle", function (e) {
    // No special init needed — event delegation handles everything
  });
})();
