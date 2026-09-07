package persistencia;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import logica.*;

public class CargarDatosPrueba {
    
    

    public static void main(String[] args) {



        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("edEXTPU");

        EntityManager em = emf.createEntityManager();

        System.out.println("CONEXION EXITOSA");

        try {

            em.getTransaction().begin();

            // =========================
            // USUARIOS
            // =========================

            Estudiante EL =
                    new Estudiante(
                            "eleven11",
                            "eleven11@gmail.com",
                            "Eleven",
                            "Twelve",
                            fecha("31/12/1971")
                    );

            Estudiante CO =
                    new Estudiante(
                            "costas",
                            "gcostas@gmail.com",
                            "Gerardo",
                            "Costas",
                            fecha("15/11/1983")
                    );

            Estudiante RO =
                    new Estudiante(
                            "roro",
                            "rcotelo@yahoo.com",
                            "Rodrigo",
                            "Cotelo",
                            fecha("02/08/1975")
                    );

            Estudiante CH =
                    new Estudiante(
                            "chechi",
                            "cgarrido@hotmail.com",
                            "Cecilia",
                            "Garrido",
                            fecha("12/09/1987")
                    );

            Estudiante JW =
                    new Estudiante(
                            "jeffw",
                            "jwilliams@gmail.com",
                            "Jeff",
                            "Williams",
                            fecha("27/11/1964")
                    );

            Estudiante WE =
                    new Estudiante(
                            "weiss",
                            "aweiss@hotmail.com",
                            "Adrian",
                            "Weiss",
                            fecha("23/12/1978")
                    );

            Docente WW =
                    new Docente(
                            "heisenberg",
                            "heisenberg@gmail.com",
                            "Walter",
                            "White",
                            fecha("07/03/1956")
                    );

            Docente OK =
                    new Docente(
                            "benkenobi",
                            "benKenobi@gmail.com",
                            "Obi-Wan",
                            "Kenobi",
                            fecha("02/04/1914")
                    );

            Docente EW =
                    new Docente(
                            "waston",
                            "e.watson@gmail.com",
                            "Emma",
                            "Watson",
                            fecha("15/04/1990")
                    );

            Docente GH =
                    new Docente(
                            "house",
                            "greghouse@gmail.com",
                            "Gregory",
                            "House",
                            fecha("15/05/1959")
                    );

            Docente TC =
                    new Docente(
                            "timmy",
                            "tim.cook@apple.com",
                            "Tim",
                            "Cook",
                            fecha("01/11/1960")
                    );

            Docente DR =
                    new Docente(
                            "danny",
                            "dan.riccio@gmail.com",
                            "Daniel",
                            "Riccio",
                            fecha("05/07/1963")
                    );

            Docente PS =
                    new Docente(
                            "phils",
                            "schiller@gmail.com",
                            "Philip",
                            "Schiller",
                            fecha("07/10/1961")
                    );

            Docente BS =
                    new Docente(
                            "bruces",
                            "sewell@gmail.com",
                            "Bruce",
                            "Sewell",
                            fecha("03/12/1959")
                    );

            Docente AG =
                    new Docente(
                            "adri",
                            "agarcia@gmail.com",
                            "Adriana",
                            "García",
                            fecha("28/07/1978")
                    );

            // =========================
            // INSTITUTOS
            // =========================

            Instituto IN =
                        new Instituto("INCO");
            Instituto IL =
                        new Instituto("IMERL");
            Instituto IF =
                        new Instituto("Física");
            Instituto IM =
                        new Instituto("IMPII");
            Instituto IE =
                        new Instituto("Eléctrica");
            Instituto DI =
                        new Instituto("DISI");

            // =========================
            // DATOS ADICIONALES DE DOCENTES
            // =========================

            WW.agregoInstituto(IN);
            OK.agregoInstituto(IN);
            EW.agregoInstituto(IN);
            GH.agregoInstituto(IE);
            TC.agregoInstituto(IL);
            DR.agregoInstituto(IL);
            PS.agregoInstituto(IM);
            BS.agregoInstituto(DI);
            AG.agregoInstituto(DI);

            // =========================
            // CURSOS
            // =========================

            Curso C1 = new Curso(
                    "Talleres plenarios",
                    "3 semanas",
                    15,
                    1,
                    fecha("01/02/2026"),
                    "*Talleres plenarios*: presentados por cuatro reconocidos\n" +
                    "matemáticos uruguayos, plantearán diversos tópicos de matemática\n" +
                    "en el marco de los cuales se realizarán actividades fomentando la\n" +
                    "integración entre estudiantes, docentes e investigadores.",
                    "www.tmu.edu.uy");
            
            Curso C2 = new Curso(
                    "Seminarios de Resolución de Problemas",
                    "5 semanas",
                    30,
                    2,
                    fecha("12/07/2026"),
                    "Seminario, *todos los jueves* en Facultad de Ingeniería a\n" +
                    "partir del jueves 25 de Julio, en las áreas en que se desarrollan los\n" +
                    "problemas de las Olimpíadas de Matemática.",
                    "www.tmu.edu.uy");
            
            C2.agregoPrevia(C1);
            
            Curso C3 = new Curso(
                    "Dalavuelta",
                    "10 semanas",
                    60,
                    4,
                    fecha("01/02/2026"),
                    "Dalavuelta es un proyecto de extensión que nace en el Instituto de\n" +
                    "Ingeniería Mecánica y Producción Industrial (IIMPI) de Fing, que, si\n" +
                    "bien inicia su trabajo en el desarrollo de bicicletas accesibles para\n" +
                    "personas en situación de discapacidad motriz a partir de bicicletas\n" +
                    "abandonadas, se propuso diseñar otras herramientas para fomentar\n" +
                    "la accesibilidad.",
                    "https://eva.fing.edu.uy/course/view.php?id=783#section-2");
            
            C3.agregoPrevia(C1);
            
            Curso C4 = new Curso(
                    "Extensionismo Industrial",
                    "12 semanas",
                    75,
                    5,
                    fecha("16/06/25"),
                    "El proyecto tiene como objetivo desarrollar intervenciones\n" +
                    "curriculares en pequeños emprendimientos productivos de\n" +
                    "diferentes sectores de la industria nacional.La metodologías de\n" +
                    "trabajo permite articular diversas intervenciones, combinando\n" +
                    "actividades de enseñanza, extensión e investigación por parte de\n" +
                    "docentes del IMPII.",
                    "https://eva.fing.edu.uy/course/view.php?id=783#section-2");
            
            C4.agregoPrevia(C1);
            
            Curso C5 = new Curso(
                    "Inclusión Energética",
                    "6 semanas",
                    45,
                    3,
                    fecha("01/02/26"),
                    "En el proyecto se conjuga el trabajo de docentes y estudiantes de la\n" +
                    "carrera Ingeniería Industrial Mecánica a través del Módulo de\n" +
                    "Extensión, en donde se trabaja en el diseño, construcción y prueba\n" +
                    "de un prototipo de colector solar adquiriendo conocimientos\n" +
                    "relevantes para luego poder replicarlos junto a las familias en los\n" +
                    "talleres. Las premisas fundamentales a la hora de pensar los diseños\n" +
                    "son: por un lado el bajo costo de los materiales y por otro la fácil\n" +
                    "construcción de forma de poder construirlos ellos mismos.",
                    "https://eva.fing.edu.uy/course/view.php?id=783#section-2");
            
            Curso C6 = new Curso(
                    "Flor del Ceibo",
                    "15 semanas",
                    150,
                    10,
                    fecha("27/07/08"),
                    "Flor de Ceibo es un proyecto central de la Universidad de la\n" +
                    "República, que tiene misión por movilizar la participación de\n" +
                    "estudiantes universitarios en diversas tareas vinculadas con la\n" +
                    "puesta en funcionamiento del Plan Ceibal en el territorio nacional.",
                    "http://www.flordeceibo.edu.uy");
            
            Curso C7 = new Curso(
                    "Taller de robótica educativa",
                    "8 semanas",
                    90,
                    6,
                    fecha("02/02/24"),
                    "La asignatura se organiza en dos etapas. La primer etapa se dicta a\n" +
                    "través de clases teóricoprácticas, donde se espera además que cada\n" +
                    "estudiante le dedique horas de estudio.\n" +
                    "La segunda etapa consiste en que los estudiantes trabajen en grupo\n" +
                    "sobre el diseño e implementación de una experiencia didáctica de\n" +
                    "inclusión del robot Butiá en el aula, utilizando los conocimientos\n" +
                    "aprendidos en clase.",
                    "https://eva.fing.edu.uy/course/view.php?id=1187");
            
            Curso C8 = new Curso(
                    "Participación en investigación\n" +
                    "sobre el empleo del juego\n" +
                    "Komikan como recurso\n" +
                    "didáctico en la Escuela",
                    "9 semanas",
                    45,
                    3,
                    fecha("15/06/26"),
                    "Se propone desarrollar una aplicación interactiva para tablet\n" +
                    "Android basada en el juego de tablero Komikan (versión web del\n" +
                    "juego https://codepen.io/Borborem/full/OvZBvZ/), que incluya los\n" +
                    "distintos aspectos concernientes al juego, así como a situaciones\n" +
                    "específicas particulares.",
                    "https://eva.fing.edu.uy/mod/folder/view.php?id=89398");
            
            Curso C9 = new Curso(
                    "“Herramientas de apoyo a la\n" +
                    "enseñanza de inglés.\n" +
                    "Instalación y evaluación”",
                    "12 semanas",
                    60,
                    4,
                    fecha("24/05/26"),
                    " Se realizarán visitas a escuelas rurales participantes en un proyecto\n" +
                    "conjunto del grupo PLN y el Programa de Políticas Lingüísticas de\n" +
                    "ANEP, en el marco del cual se desarrollaron diferentes herramientas\n" +
                    "para uso de maestros que enseñan inglés con apoyo remoto de\n" +
                    "profesores especializados desde Montevideo.",
                    "https://eva.fing.edu.uy/mod/folder/view.php?id=89398");
            
            Curso C10 = new Curso(
                    "MicroBit",
                    "15 semanas",
                    105,
                    7,
                    fecha("13/03/26"),
                    "El Centro Ceibal se encuentra distribuyendo placas micro:bit\n" +
                    "(https://microbit.ceibal.edu.uy/) para que estudiantes de primaria\n" +
                    "y secundaria aprendan nociones básicas de robótica, electrónica y\n" +
                    "programación de forma autónoma y lúdica. Estas placas se basan en\n" +
                    "un microcontrolador y cuentan con leds, botones, acelerómetro,\n" +
                    "brújula, bluetooth y otros sensores. Además, las placas se\n" +
                    "programan fácilmente con lenguaje tipo “scratch” y python, por lo\n" +
                    "que son muy útiles para un primer acercamiento a la temática.",
                    "https://www.fing.edu.uy/noticias/extension/modulo-detallerextensionmicrobit");
            
            // =========================
            // EDICIONES DE CURSOS
            // =========================
            
            EdicionCurso E1 = new EdicionCurso(
                    "Flor del Ceibo - 2010",
                    fecha("15/03/10"),
                    fecha("07/07/10"),
                    -1,
                    fecha("16/02/10")
                    );
            
            EdicionCurso E2 = new EdicionCurso(
                    "Flor del Ceibo - 2012",
                    fecha("01/08/12"),
                    fecha("20/11/12"),
                    -1,
                    fecha("10/07/12")
                    );
            
            EdicionCurso E3 = new EdicionCurso(
                    "Flor del Ceibo - 2025",
                    fecha("10/04/25"),
                    fecha("07/08/25"),
                    -1,
                    fecha("06/03/25")
                    );
            
            EdicionCurso E4 = new EdicionCurso(
                    "Dalavuelta - 2025",
                    fecha("20/08/24"),
                    fecha("10/11/24"),
                    15,
                    fecha("20/07/24")
                    );
            
            EdicionCurso E5 = new EdicionCurso(
                    "Extensionismo Industrial - 2025",
                    fecha("10/08/25"),
                    fecha("10/11/25"),
                    15,
                    fecha("08/07/25")
                    );
            
            EdicionCurso E6 = new EdicionCurso(
                    "Inclusión Energética - 2026",
                    fecha("15/03/26"),
                    fecha("30/04/26"),
                    30,
                    fecha("20/02/26")
                    );
            
            EdicionCurso E7 = new EdicionCurso(
                    "Taller de robótica educativa - 2024",
                    fecha("10/03/24"),
                    fecha("10/05/24"),
                    10,
                    fecha("15/02/24")
                    );
            
            EdicionCurso E8 = new EdicionCurso(
                    "Taller de robótica educativa - 2026",
                    fecha("10/03/26"),
                    fecha("10/05/26"),
                    10,
                    fecha("15/02/26")
                    );
            
            EdicionCurso E9 = new EdicionCurso(
                    "Taller de robótica educativa - 2026-2",
                    fecha("10/09/26"),
                    fecha("08/11/26"),
                    20,
                    fecha("15/08/26")
                    );
            
            EdicionCurso E10 = new EdicionCurso(
                    "Participación en investigación sobre\n" +
                    "el empleo del juego Komikan como\n" +
                    "recurso didáctico en la Escuela - 2026",
                    fecha("29/07/26"),
                    fecha("07/10/26"),
                    5,
                    fecha("10/07/26")
                    );
            
            EdicionCurso E11 = new EdicionCurso(
                    "Herramientas de apoyo a la\n" +
                    "enseñanza de inglés. Instalación y\n" +
                    "evaluación - 26",
                    fecha("15/09/26"),
                    fecha("15/12/26"),
                    5,
                    fecha("02/06/26")
                    );
            
            EdicionCurso E12 = new EdicionCurso(
                    "MicroBit - 2026",
                    fecha("12/08/26"),
                    fecha("05/12/26"),
                    30,
                    fecha("02/07/26")
                    );
            
            EdicionCurso E13 = new EdicionCurso(
                    "Talleres plenarios - 2026",
                    fecha("10/03/26"),
                    fecha("30/03/26"),
                    -1,
                    fecha("02/03/26")
                    );
            
            EdicionCurso E14 = new EdicionCurso(
                    "Seminarios de Resolución de\n" +
                    "Problemas - 2026",
                    fecha("10/09/26"),
                    fecha("20/10/26"),
                    -1,
                    fecha("12/07/26")
                    );
            
            C6.agregoEdicion(E1);
            E1.setCurso(C6);
            C6.agregoEdicion(E2);
            E2.setCurso(C6);
            C6.agregoEdicion(E3);
            E3.setCurso(C6);
            C3.agregoEdicion(E4);
            E4.setCurso(C3);
            C4.agregoEdicion(E5);
            E5.setCurso(C4);
            C5.agregoEdicion(E6);
            E6.setCurso(C5);
            C7.agregoEdicion(E7);
            E7.setCurso(C7);
            C7.agregoEdicion(E8);
            E8.setCurso(C7);
            C7.agregoEdicion(E9);
            E9.setCurso(C7);
            C8.agregoEdicion(E10);
            E10.setCurso(C8);
            C9.agregoEdicion(E11);
            E11.setCurso(C9);
            C10.agregoEdicion(E12);
            E12.setCurso(C10);
            C1.agregoEdicion(E13);
            E13.setCurso(C1);
            C2.agregoEdicion(E14);
            E14.setCurso(C2);
            
            E1.agregoDocente(BS);
            E2.agregoDocente(BS);
            E2.agregoDocente(AG);
            E3.agregoDocente(BS);
            E3.agregoDocente(AG);
            E4.agregoDocente(PS);
            E5.agregoDocente(PS);
            E6.agregoDocente(PS);
            E7.agregoDocente(WW);
            E8.agregoDocente(WW);
            E8.agregoDocente(OK);
            E9.agregoDocente(OK);
            E9.agregoDocente(EW);
            E10.agregoDocente(EW);
            E11.agregoDocente(WW);
            E12.agregoDocente(GH);
            E13.agregoDocente(TC);
            E13.agregoDocente(DR);
            E14.agregoDocente(TC);
            
            BS.agregoEdicion(E1);
            BS.agregoEdicion(E2);
            BS.agregoEdicion(E3);
            AG.agregoEdicion(E2);
            AG.agregoEdicion(E3);
            PS.agregoEdicion(E4);
            PS.agregoEdicion(E5);
            PS.agregoEdicion(E6);
            WW.agregoEdicion(E7);
            WW.agregoEdicion(E8);
            OK.agregoEdicion(E8);
            OK.agregoEdicion(E9);
            EW.agregoEdicion(E9);
            EW.agregoEdicion(E10);
            WW.agregoEdicion(E11);
            GH.agregoEdicion(E12);
            TC.agregoEdicion(E13);
            DR.agregoEdicion(E13);
            TC.agregoEdicion(E14);
            
            // =========================
            // INSCRIPCIONES A EDICIONES DE CURSOS
            // =========================
            
            Inscripcion I1 = new Inscripcion(fecha("20/02/10"), E1);
            EL.agregoInscripcion(I1);
            
            Inscripcion I2 = new Inscripcion(fecha("25/02/10"), E1);
            CH.agregoInscripcion(I2);
            
            Inscripcion I3 = new Inscripcion(fecha("12/07/12"), E2);
            CO.agregoInscripcion(I3);
            
            Inscripcion I4 = new Inscripcion(fecha("15/07/12"), E2);
            RO.agregoInscripcion(I4);
            
            Inscripcion I5 = new Inscripcion(fecha("30/07/12"), E2);
            WE.agregoInscripcion(I5);
            
            Inscripcion I6 = new Inscripcion(fecha("10/03/25"), E3);
            RO.agregoInscripcion(I6);
            
            Inscripcion I7 = new Inscripcion(fecha("15/03/25"), E3);
            JW.agregoInscripcion(I7);
            
            Inscripcion I8 = new Inscripcion(fecha("25/07/24"), E4);
            CH.agregoInscripcion(I8);
            
            Inscripcion I9 = new Inscripcion(fecha("28/07/24"), E4);
            EL.agregoInscripcion(I9);
            
            Inscripcion I10 = new Inscripcion(fecha("02/08/24"), E4);
            RO.agregoInscripcion(I10);
            
            Inscripcion I11 = new Inscripcion(fecha("10/08/24"), E4);
            CO.agregoInscripcion(I11);
            
            Inscripcion I12 = new Inscripcion(fecha("15/08/24"), E4);
            JW.agregoInscripcion(I12);
            
            Inscripcion I13 = new Inscripcion(fecha("18/07/25"), E5);
            CO.agregoInscripcion(I13);
            
            Inscripcion I14 = new Inscripcion(fecha("20/07/25"), E5);
            CH.agregoInscripcion(I14);
            
            Inscripcion I15 = new Inscripcion(fecha("29/07/25"), E5);
            EL.agregoInscripcion(I15);
            
            Inscripcion I16 = new Inscripcion(fecha("05/08/25"), E5);
            WE.agregoInscripcion(I16);
            
            Inscripcion I17 = new Inscripcion(fecha("23/02/26"), E6);
            RO.agregoInscripcion(I17);
            
            Inscripcion I18 = new Inscripcion(fecha("25/02/26"), E6);
            WE.agregoInscripcion(I18);
            
            Inscripcion I19 = new Inscripcion(fecha("28/02/26"), E6);
            CH.agregoInscripcion(I19);
            
            Inscripcion I20 = new Inscripcion(fecha("03/03/26"), E6);
            EL.agregoInscripcion(I20);
            
            Inscripcion I21 = new Inscripcion(fecha("18/02/17"), E7);
            WE.agregoInscripcion(I21);
            
            Inscripcion I22 = new Inscripcion(fecha("20/02/24"), E7);
            RO.agregoInscripcion(I22);
            
            Inscripcion I23 = new Inscripcion(fecha("03/03/24"), E7);
            EL.agregoInscripcion(I23);
            
            Inscripcion I24 = new Inscripcion(fecha("05/03/24"), E7);
            CH.agregoInscripcion(I24);
            
            Inscripcion I25 = new Inscripcion(fecha("18/02/26"), E8);
            JW.agregoInscripcion(I25);
            
            Inscripcion I26 = new Inscripcion(fecha("22/02/26"), E8);
            CO.agregoInscripcion(I26);
            
            Inscripcion I27 = new Inscripcion(fecha("18/08/26"), E9);
            WE.agregoInscripcion(I27);
            
            Inscripcion I28 = new Inscripcion(fecha("22/08/26"), E9);
            CH.agregoInscripcion(I28);
            
            Inscripcion I29 = new Inscripcion(fecha("03/09/26"), E9);
            RO.agregoInscripcion(I29);
            
            Inscripcion I30 = new Inscripcion(fecha("13/07/26"), E10);
            CH.agregoInscripcion(I30);
            
            Inscripcion I31 = new Inscripcion(fecha("20/07/26"), E10);
            WE.agregoInscripcion(I31);
            
            Inscripcion I32 = new Inscripcion(fecha("22/07/26"), E10);
            RO.agregoInscripcion(I32);
            
            Inscripcion I33 = new Inscripcion(fecha("04/06/26"), E11);
            WE.agregoInscripcion(I33);
            
            Inscripcion I34 = new Inscripcion(fecha("18/07/26"), E11);
            EL.agregoInscripcion(I34);
            
            Inscripcion I35 = new Inscripcion(fecha("20/08/26"), E11);
            JW.agregoInscripcion(I35);
            
            Inscripcion I36 = new Inscripcion(fecha("12/07/26"), E12);
            CH.agregoInscripcion(I36);
            
            Inscripcion I37 = new Inscripcion(fecha("14/07/26"), E12);
            RO.agregoInscripcion(I37);
            
            Inscripcion I38 = new Inscripcion(fecha("25/07/26"), E12);
            EL.agregoInscripcion(I38);
            
            Inscripcion I39 = new Inscripcion(fecha("05/08/26"), E12);
            JW.agregoInscripcion(I39);
            
            Inscripcion I40 = new Inscripcion(fecha("05/03/26"), E13);
            CO.agregoInscripcion(I40);
            
            Inscripcion I41 = new Inscripcion(fecha("04/03/26"), E13);
            WE.agregoInscripcion(I41);
            
            Inscripcion I42 = new Inscripcion(fecha("07/03/26"), E13);
            RO.agregoInscripcion(I42);
            
            Inscripcion I43 = new Inscripcion(fecha("15/07/26"), E14);
            WE.agregoInscripcion(I43);
            
            Inscripcion I44 = new Inscripcion(fecha("20/07/26"), E14);
            CO.agregoInscripcion(I44);
            
            Inscripcion I45 = new Inscripcion(fecha("06/08/26"), E14);
            RO.agregoInscripcion(I45);
            
            Inscripcion I46 = new Inscripcion(fecha("30/08/26"), E14);
            CH.agregoInscripcion(I46);
            
            
            // =========================
            // PROGRAMAS DE FORMACION
            // =========================
            
            
            ProgramaFormacion P1 = new ProgramaFormacion(
                    "EFI Ingeniería Mecánica",
                    "Programa mecánica",
                    fecha("01/05/26"),
                    fecha("31/10/26"),
                    new Date()
            );
            
            P1.agregarCurso(C3);
            P1.agregarCurso(C4);
            P1.agregarCurso(C5);
            
            ProgramaFormacion P2 = new ProgramaFormacion(
                    "Formación integral",
                    "Programa varios institutos",
                    fecha("15/07/26"),
                    fecha("01/01/27"),
                    new Date()
            );
            
            P2.agregarCurso(C2);
            P2.agregarCurso(C4);
            P2.agregarCurso(C6);
            P2.agregarCurso(C8);
            
            
            ProgramaFormacion P3 = new ProgramaFormacion(
                    "EFI Robótica",
                    "Programa robótica",
                    fecha("03/09/26"),
                    fecha("18/11/26"),
                    new Date()
            );
            
            P3.agregarCurso(C7);
            P3.agregarCurso(C10);
            
            
            
            
            
            

            //em.persist();



            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {

            em.close();
            emf.close();
        }
    }
    
    private static Date fecha(String f) {
    try {
        return new SimpleDateFormat("dd/MM/yyyy").parse(f);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}


}