(() => {
    const instituto = document.getElementById('instituto');
    if (!instituto) return;
    const grupos = document.querySelectorAll('[data-previas-instituto]');
    const aviso = document.getElementById('previas-sin-instituto');
    const actualizar = () => {
        for (const grupo of grupos) {
            const activo = grupo.dataset.previasInstituto === instituto.value;
            grupo.hidden = !activo;
            for (const opcion of grupo.querySelectorAll('input[name="previas"]')) {
                opcion.disabled = !activo;
                if (!activo) opcion.checked = false;
            }
        }
        if (aviso) aviso.hidden = instituto.value !== '';
    };
    instituto.addEventListener('change', actualizar);
    actualizar();
})();
