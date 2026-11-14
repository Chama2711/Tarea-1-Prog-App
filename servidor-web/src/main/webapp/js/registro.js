(() => {
    const opciones = document.querySelectorAll('input[name="tipo"]');
    const instituto = document.getElementById('instituto');
    const mostrarClave = document.getElementById('mostrarClave');
    const imagen = document.getElementById('imagen');
    const vista = document.getElementById('foto-preview');
    const avatar = vista.src;
    let urlImagen;

    function actualizarTipo() {
        const docente = document.querySelector('input[name="tipo"]:checked').value === 'docente';
        document.getElementById('campoInstituto').hidden = !docente;
        instituto.disabled = !docente;
        instituto.required = docente;
        document.getElementById('ayuda-tipo').textContent = docente
            ? 'Como docente, podés crear cursos y acompañar estudiantes.'
            : 'Como estudiante, podés inscribirte a cursos.';
    }
    opciones.forEach(opcion => opcion.addEventListener('change', actualizarTipo));
    actualizarTipo();
    mostrarClave.addEventListener('change', () => {
        ['clave', 'confirmacion'].forEach(id => {
            document.getElementById(id).type = mostrarClave.checked ? 'text' : 'password';
        });
    });
    imagen.addEventListener('change', () => {
        if (urlImagen) URL.revokeObjectURL(urlImagen);
        vista.src = avatar;
        const archivo = imagen.files[0];
        imagen.setCustomValidity('');
        document.getElementById('nombre-archivo').textContent = archivo ? archivo.name : '';
        if (!archivo) return;
        if (archivo.size > 5 * 1024 * 1024) {
            imagen.setCustomValidity('La imagen no puede superar los 5 MB.');
            imagen.reportValidity();
            return;
        }
        urlImagen = URL.createObjectURL(archivo);
        vista.src = urlImagen;
    });
    vista.addEventListener('error', () => {
        if (vista.src !== avatar) {
            vista.src = avatar;
            imagen.setCustomValidity('Seleccioná una imagen JPG o PNG válida.');
            imagen.reportValidity();
        }
    });
    window.addEventListener('pagehide', () => {
        if (urlImagen) URL.revokeObjectURL(urlImagen);
    });
})();
