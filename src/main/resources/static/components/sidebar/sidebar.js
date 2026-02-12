(function () {
  function init() {
    const sidebar = document.getElementById('sidebar');
    const sidebarSwitcher = document.getElementById('sidebar-switcher');
    const floatingSidebar = document.getElementById('floating-sidebar');

    if (sidebarSwitcher != null) {
        const icon = sidebarSwitcher.querySelector("i");
        sidebarSwitcher.addEventListener('click', () => {
            if (sidebar.classList.contains("sidebar-collapsed")) {
                icon.classList.remove("fa-arrow-right");
                icon.classList.add("fa-arrow-left");
            } else {
                icon.classList.remove("fa-arrow-left");
                icon.classList.add("fa-arrow-right");
            }
            sidebar.classList.toggle('sidebar-collapsed');
        });
    }

    let isFloating = false;
    let hoveringFloating = false;

    if (floatingSidebar != null) {
        floatingSidebar.addEventListener('mouseenter', () => {
            hoveringFloating = true;
        });
        floatingSidebar.addEventListener('mouseleave', () => {
            hoveringFloating = false;
            if (isFloating) {
                isFloating = false;
                floatingSidebar.classList.remove('show');
            }
        });

        document.addEventListener('mousemove', (event) => {
            const threshold = 10;

            if (!sidebar.classList.contains('sidebar-collapsed')) return;

            if (event.clientX <= threshold && event.clientY >= 60) {
                if (!isFloating) {
                    isFloating = true;
                    floatingSidebar.classList.add('show');
                }
            }
            else if (!hoveringFloating) {
                if (isFloating) {
                    isFloating = false;
                    floatingSidebar.classList.remove('show');
                }
            }
        });
    }
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
