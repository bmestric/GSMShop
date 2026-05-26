const COMPARE_KEY = 'gsmCompareIds';
const MAX_COMPARE = 4;

function getIds() {
    return JSON.parse(localStorage.getItem(COMPARE_KEY) || '[]');
}

function saveIds(ids) {
    localStorage.setItem(COMPARE_KEY, JSON.stringify(ids));
}

function togglePhone(id) {
    let ids = getIds();
    const idx = ids.indexOf(id);
    if (idx >= 0) {
        ids.splice(idx, 1);
    } else if (ids.length < MAX_COMPARE) {
        ids.push(id);
    } else {
        showMaxWarning();
        return;
    }
    saveIds(ids);
    updateCompareUI();
}

function showMaxWarning() {
    const warn = document.getElementById('compareMaxWarn');
    if (warn) {
        warn.classList.remove('d-none');
        setTimeout(() => warn.classList.add('d-none'), 3000);
    }
}

function updateCompareUI() {
    const ids = getIds();

    document.querySelectorAll('.compare-check').forEach(cb => {
        const id = parseInt(cb.dataset.phoneId, 10);
        cb.checked = ids.includes(id);
    });

    const btn = document.getElementById('compareFloatBtn');
    if (!btn) return;

    if (ids.length >= 2) {
        btn.style.display = 'flex';
        btn.querySelector('.compare-count').textContent = ids.length;
        btn.href = '/compare?ids=' + ids.join(',');
    } else {
        btn.style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    updateCompareUI();

    document.querySelectorAll('.compare-check').forEach(cb => {
        cb.addEventListener('change', () => {
            togglePhone(parseInt(cb.dataset.phoneId, 10));
        });
    });
});
