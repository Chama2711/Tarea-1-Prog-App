(() => {
    const foto = document.querySelector('.foto-ampliable');
    const visor = document.getElementById('visor-foto');
    if (foto && visor) {
        const abrirFoto = () => {
            if (visor.open) return;
            document.getElementById('foto-ampliada').src = foto.currentSrc || foto.src;
            visor.showModal();
        };
        foto.addEventListener('dblclick', abrirFoto);
        foto.addEventListener('keydown', evento => {
            if (evento.key === 'Enter' || evento.key === ' ') {
                evento.preventDefault();
                abrirFoto();
            }
        });
        visor.addEventListener('click', evento => {
            const limites = visor.getBoundingClientRect();
            if (evento.target === visor && (evento.clientX < limites.left
                    || evento.clientX > limites.right || evento.clientY < limites.top
                    || evento.clientY > limites.bottom)) visor.close();
        });
        visor.addEventListener('close', () => foto.focus({ preventScroll: true }));
    }
    const barra = document.querySelector('.perfil-pestanas');
    if (!barra) return;
    const enlaces = Array.from(barra.querySelectorAll('a'));
    const paneles = enlaces.map(enlace => document.getElementById(enlace.hash.slice(1)));
    if (paneles.some(panel => !panel)) return;

    function seleccionar(indice, enfocar = false) {
        enlaces.forEach((enlace, i) => {
            enlace.setAttribute('aria-selected', String(i === indice));
            enlace.tabIndex = i === indice ? 0 : -1;
            paneles[i].hidden = i !== indice;
        });
        if (enfocar) enlaces[indice].focus();
    }

    barra.setAttribute('role', 'tablist');
    enlaces.forEach((enlace, indice) => {
        enlace.setAttribute('role', 'tab');
        enlace.setAttribute('aria-controls', paneles[indice].id);
        paneles[indice].setAttribute('role', 'tabpanel');
        paneles[indice].setAttribute('aria-labelledby', enlace.id);
        paneles[indice].tabIndex = 0;
        enlace.addEventListener('click', evento => {
            evento.preventDefault();
            seleccionar(indice);
        });
        enlace.addEventListener('keydown', evento => {
            let siguiente;
            if (evento.key === 'ArrowRight') siguiente = (indice + 1) % enlaces.length;
            if (evento.key === 'ArrowLeft') siguiente = (indice - 1 + enlaces.length) % enlaces.length;
            if (evento.key === 'Home') siguiente = 0;
            if (evento.key === 'End') siguiente = enlaces.length - 1;
            if (siguiente === undefined) return;
            evento.preventDefault();
            seleccionar(siguiente, true);
        });
    });
    const inicial = enlaces.findIndex(enlace => enlace.hash === window.location.hash);
    seleccionar(inicial < 0 ? 0 : inicial);
})();
