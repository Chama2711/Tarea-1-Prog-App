(() => {
    const formulario = document.getElementById('form-seleccion');
    if (!formulario) return;
    const selectores = Array.from(formulario.querySelectorAll('select'));
    let cambios = selectores.some(select => select.value !== '');
    formulario.addEventListener('change', () => {
        cambios = selectores.some(select => select.value !== '');
    });
    formulario.addEventListener('reset', evento => {
        evento.preventDefault();
        selectores.forEach(select => { select.value = ''; });
        cambios = false;
    });
    formulario.addEventListener('submit', () => { cambios = false; });
    document.querySelectorAll('.filtro-seleccion').forEach(filtro => {
        filtro.addEventListener('submit', evento => {
            if (cambios && !window.confirm('Tenés decisiones sin guardar. ¿Querés descartarlas y cambiar la consulta?')) {
                evento.preventDefault();
                return;
            }
            cambios = false;
        });
    });
    window.addEventListener('beforeunload', evento => {
        if (cambios) { evento.preventDefault(); evento.returnValue = ''; }
    });
})();
