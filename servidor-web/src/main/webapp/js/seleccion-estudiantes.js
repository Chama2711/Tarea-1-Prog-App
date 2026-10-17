(() => {
    const filtro = document.getElementById('filtro-estudiantes');
    const formulario = document.getElementById('form-seleccion');

    // Detectar decisiones pendientes sin guardar.
    const selectores = formulario
        ? Array.from(formulario.querySelectorAll('select'))
        : [];

    let cambios = selectores.some(select => select.value !== '');

    if (formulario) {
        formulario.addEventListener('change', () => {
            cambios = selectores.some(select => select.value !== '');
        });

        formulario.addEventListener('reset', evento => {
            evento.preventDefault();

            selectores.forEach(select => {
                select.value = '';
            });

            cambios = false;
        });

        formulario.addEventListener('submit', () => {
            cambios = false;
        });
    }

    if (filtro) {
        const instituto = document.getElementById('instituto');
        const curso = document.getElementById('curso');

        const institutoAnterior = instituto.value;
        const cursoAnterior = curso.value;

        function actualizarFormulario(cambioInstituto) {

            if (cambios && !window.confirm(
                'Tenés decisiones sin guardar. ¿Querés descartarlas y cambiar la consulta?'
            )) {
                if (cambioInstituto) {
                    instituto.value = institutoAnterior;
                } else {
                    curso.value = cursoAnterior;
                }
                return;
            }

            cambios = false;

            const parametros = new URLSearchParams();

            if (instituto.value) {
                parametros.set('instituto', instituto.value);
            }

            if (!cambioInstituto && curso.value) {
                parametros.set('curso', curso.value);
            }

            const orden = filtro.querySelector(
                'input[name="orden"]:checked'
            );

            if (orden) {
                parametros.set('orden', orden.value);
            }

            window.location.assign(
                filtro.action + '?' + parametros.toString()
            );
        }

        instituto.addEventListener('change', () => {
            actualizarFormulario(true);
        });

        curso.addEventListener('change', () => {
            actualizarFormulario(false);
        });

        // Actualizar automáticamente el orden de las inscripciones.
        const ordenAnterior = new URLSearchParams(
            window.location.search
        ).get('orden') || 'FECHA';

        const opcionesOrden = filtro.querySelectorAll(
            'input[name="orden"]'
        );

        opcionesOrden.forEach(opcion => {
            opcion.addEventListener('change', () => {

                const edicion = document.getElementById('edicion');

                // Solo actualizar cuando hay una edición seleccionada.
                if (!edicion || !edicion.value) {
                    return;
                }

                // Proteger decisiones pendientes de guardar.
                if (cambios && !window.confirm(
                    'Tenés decisiones sin guardar. ¿Querés descartarlas y cambiar el orden?'
                )) {
                    const anterior = Array.from(opcionesOrden).find(
                        radio => radio.value === ordenAnterior
                    );

                    if (anterior) {
                        anterior.checked = true;
                    }

                    return;
                }

                cambios = false;

                // Conservar instituto, curso y edición seleccionados.
                const parametros = new URLSearchParams(
                    new FormData(filtro)
                );

                // Recargar la lista con el nuevo criterio.
                window.location.assign(
                    filtro.action + '?' + parametros.toString()
                );
            });
        });





        filtro.addEventListener('submit', evento => {
            if (cambios && !window.confirm(
                'Tenés decisiones sin guardar. ¿Querés descartarlas y cambiar la consulta?'
            )) {
                evento.preventDefault();
                return;
            }

            cambios = false;
        });
    }

    window.addEventListener('beforeunload', evento => {
        if (cambios) {
            evento.preventDefault();
            evento.returnValue = '';
        }
    });
})();