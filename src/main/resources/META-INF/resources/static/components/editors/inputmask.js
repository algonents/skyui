(function () {
  function init() {
    Inputmask
        .extendAliases({
            latitudeDMS: {
                mask: "99°99'99\"N",
                definitions: {'N': { validator: "[NSns]", casing: "upper", placeholder: "_" }},
                placeholder: "_",
                clearIncomplete: true,
                showMaskOnFocus: true,
                showMaskOnHover: false
            }
        });
    Inputmask().mask(document.querySelectorAll(".latitude-dms"));
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
