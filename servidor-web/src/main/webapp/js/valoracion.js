function enviarValoracion() {
    const form = document.getElementById('formValoracion');
    const formData = new URLSearchParams(new FormData(form));
    const msgDiv = document.getElementById('mensajeValoracion');

    fetch('/servidor-web/ValorarCurso', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: formData.toString()
    })
    .then(response => {
        if (!response.ok) throw new Error('Error al guardar la valoración.');
        return response.text();
    })
    .then(data => {
        msgDiv.className = "text-success fw-bold";
        msgDiv.innerText = "¡Valoración guardada correctamente!";
        setTimeout(() => location.reload(), 1200); // Recarga para actualizar promedios
    })
    .catch(error => {
        msgDiv.className = "text-danger fw-bold";
        msgDiv.innerText = error.message;
    });
}