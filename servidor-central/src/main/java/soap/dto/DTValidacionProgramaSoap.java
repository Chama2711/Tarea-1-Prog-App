/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import logica.ValidadorAprobacionPrograma.ResultadoPrograma;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTValidacionProgramaSoap {

    private String estudiante;
    private String programa;
    private boolean aprobado;
    private int totalCursos;
    private int cursosAprobados;

    @XmlElement(name = "resultadoCurso")
    private DTResultadoCursoProgramaSoap[] resultados;

    public DTValidacionProgramaSoap() {
        // Requerido por JAXB.
    }

    public static DTValidacionProgramaSoap desde(
            ResultadoPrograma origen) {

        if (origen == null) {
            throw new IllegalArgumentException(
                    "El resultado del programa es obligatorio.");
        }

        DTValidacionProgramaSoap dto =
                new DTValidacionProgramaSoap();

        dto.estudiante = origen.estudiante();
        dto.programa = origen.programa();
        dto.aprobado = origen.aprobado();
        dto.totalCursos = origen.totalCursos();
        dto.cursosAprobados = origen.cursosAprobados();

        dto.resultados = origen.resultados().stream()
                .map(DTResultadoCursoProgramaSoap::desde)
                .toArray(DTResultadoCursoProgramaSoap[]::new);

        return dto;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public String getPrograma() {
        return programa;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public int getTotalCursos() {
        return totalCursos;
    }

    public int getCursosAprobados() {
        return cursosAprobados;
    }

    public DTResultadoCursoProgramaSoap[] getResultados() {
        return resultados == null
                ? new DTResultadoCursoProgramaSoap[0]
                : resultados.clone();
    }
}

