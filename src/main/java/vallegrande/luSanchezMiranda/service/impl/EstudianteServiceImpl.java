package vallegrande.luSanchezMiranda.service.impl;

import vallegrande.luSanchezMiranda.model.Estudiante;
import vallegrande.luSanchezMiranda.repository.EstudianteRepository;
import vallegrande.luSanchezMiranda.service.EstudianteService;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;

    @Override
    public List<Estudiante> listar() {
        return estudianteRepository.findByStatusTrue();
    }

    @Override
    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }

    @Override
    public Optional<Estudiante> buscarPorId(Integer id) {
        return estudianteRepository.findById(id);
    }

    @Override
    public Estudiante guardar(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }

    @Override
    public boolean eliminar(Integer id) {
        Optional<Estudiante> opt = estudianteRepository.findById(id);
        if (opt.isPresent()) {
            Estudiante e = opt.get();
            e.setStatus(false);
            e.setDeletedAt(LocalDateTime.now());
            e.setRestoredAt(null);
            estudianteRepository.save(e);
            return true;
        }
        return false;
    }

    @Override
    public boolean restaurar(Integer id) {
        Optional<Estudiante> opt = estudianteRepository.findById(id);
        if (opt.isPresent()) {
            Estudiante e = opt.get();
            e.setStatus(true);
            e.setRestoredAt(LocalDateTime.now());
            e.setDeletedAt(null);
            estudianteRepository.save(e);
            return true;
        }
        return false;
    }

    @Override
    public int importarDesdeCSV(MultipartFile archivo) throws Exception {
        int contador = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] campos = linea.split(",");
                if (campos.length < 6) continue;
                // Salta la cabecera
                if (campos[0].trim().equalsIgnoreCase("first_name")) continue;

                Estudiante e = new Estudiante();
                e.setFirstName(campos[0].trim());
                e.setLastName(campos[1].trim());
                e.setEmail(campos[2].trim());
                e.setDocumentType(campos[3].trim());
                e.setNumberType(campos[4].trim());
                e.setPhone(campos[5].trim());
                e.setStatus(true);

                estudianteRepository.save(e);
                contador++;
            }
        }
        return contador;
    }

    @Override
    public byte[] exportarExcel() throws Exception {
        List<Estudiante> estudiantes = estudianteRepository.findByStatusTrue();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("Estudiantes");

            String[] columnas = {"ID", "Nombre", "Apellido", "Email", "Tipo Doc.", "Nro. Doc.", "Telefono"};
            Row cabecera = hoja.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                cabecera.createCell(i).setCellValue(columnas[i]);
            }

            int numFila = 1;
            for (Estudiante e : estudiantes) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(e.getStudentId());
                fila.createCell(1).setCellValue(e.getFirstName());
                fila.createCell(2).setCellValue(e.getLastName());
                fila.createCell(3).setCellValue(e.getEmail());
                fila.createCell(4).setCellValue(e.getDocumentType());
                fila.createCell(5).setCellValue(e.getNumberType());
                fila.createCell(6).setCellValue(e.getPhone());
            }

            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public byte[] exportarPDF() throws Exception {
        List<Estudiante> estudiantes = estudianteRepository.findByStatusTrue();

        ClassPathResource resource = new ClassPathResource("templates/estudiantes-reporte.html");
        String html;
        try (InputStream is = resource.getInputStream()) {
            html = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }

        StringBuilder rowsBuilder = new StringBuilder();
        for (Estudiante e : estudiantes) {
            rowsBuilder.append("<tr>")
                    .append("<td>").append(e.getStudentId()).append("</td>")
                    .append("<td>").append(e.getFirstName()).append("</td>")
                    .append("<td>").append(e.getLastName()).append("</td>")
                    .append("<td>").append(e.getEmail()).append("</td>")
                    .append("<td>").append(e.getDocumentType()).append("</td>")
                    .append("<td>").append(e.getNumberType()).append("</td>")
                    .append("<td>").append(e.getPhone()).append("</td>")
                    .append("</tr>");
        }

        html = html.replace("{{ROWS}}", rowsBuilder.toString());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4.rotate()); // landscape para más columnas
        PdfWriter.getInstance(documento, out);
        documento.open();

        HTMLWorker htmlWorker = new HTMLWorker(documento);
        try (StringReader sr = new StringReader(html)) {
            htmlWorker.parse(sr);
        }

        documento.close();
        return out.toByteArray();
    }
}
