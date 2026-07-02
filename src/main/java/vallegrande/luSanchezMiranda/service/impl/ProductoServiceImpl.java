package vallegrande.luSanchezMiranda.service.impl;

import vallegrande.luSanchezMiranda.model.Producto;
import vallegrande.luSanchezMiranda.repository.ProductoRepository;
import vallegrande.luSanchezMiranda.service.ProductoService;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public List<Producto> listar() {
        return productoRepository.findByEstadoTrue();
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public boolean eliminar(Integer id) {
        Optional<Producto> opt = productoRepository.findById(id);
        if (opt.isPresent()) {
            Producto p = opt.get();
            p.setEstado(false);
            p.setFechaEliminacion(java.time.LocalDateTime.now());
            productoRepository.save(p);
            return true;
        }
        return false;
    }

    @Override
    public boolean restaurar(Integer id) {
        Optional<Producto> opt = productoRepository.findById(id);
        if (opt.isPresent()) {
            Producto p = opt.get();
            p.setEstado(true);
            p.setFechaRestauracion(java.time.LocalDateTime.now());
            p.setFechaEliminacion(null);
            productoRepository.save(p);
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
                if (campos.length < 5) continue;
                if (campos[0].trim().equalsIgnoreCase("nombre")) continue;

                Producto p = new Producto();
                p.setNombre(campos[0].trim());
                p.setDescripcion(campos[1].trim());
                p.setCategoria(campos[2].trim());
                p.setPrecio(new BigDecimal(campos[3].trim()));
                p.setStock(Integer.parseInt(campos[4].trim()));
                p.setEstado(true);

                productoRepository.save(p);
                contador++;
            }
        }
        return contador;
    }

    @Override
    public int importarDesdeExcel(MultipartFile archivo) throws Exception {
        int contador = 0;
        try (XSSFWorkbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet hoja = workbook.getSheetAt(0);

            for (Row fila : hoja) {
                if (fila.getRowNum() == 0) continue;

                Cell celdaNombre = fila.getCell(0);
                if (celdaNombre == null || celdaNombre.getStringCellValue().trim().isEmpty()) {
                    break;
                }

                Producto p = new Producto();
                p.setNombre(celdaNombre.getStringCellValue().trim());

                Cell celdaDesc = fila.getCell(1);
                p.setDescripcion(celdaDesc != null ? celdaDesc.getStringCellValue().trim() : "");

                Cell celdaCat = fila.getCell(2);
                p.setCategoria(celdaCat != null ? celdaCat.getStringCellValue().trim() : "Otros");

                Cell celdaPrecio = fila.getCell(3);
                p.setPrecio(celdaPrecio != null ? new BigDecimal(celdaPrecio.getNumericCellValue()) : BigDecimal.ZERO);

                Cell celdaStock = fila.getCell(4);
                p.setStock(celdaStock != null ? (int) celdaStock.getNumericCellValue() : 0);

                p.setEstado(true);

                productoRepository.save(p);
                contador++;
            }
        }
        return contador;
    }

    @Override
    public byte[] exportarExcel() throws Exception {
        List<Producto> productos = productoRepository.findByEstadoTrue();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("Productos");

            Row cabecera = hoja.createRow(0);
            String[] columnas = {"ID", "Nombre", "Descripcion", "Categoria", "Precio", "Stock"};
            for (int i = 0; i < columnas.length; i++) {
                cabecera.createCell(i).setCellValue(columnas[i]);
            }

            int numFila = 1;
            for (Producto p : productos) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(p.getIdProducto());
                fila.createCell(1).setCellValue(p.getNombre());
                fila.createCell(2).setCellValue(p.getDescripcion() != null ? p.getDescripcion() : "");
                fila.createCell(3).setCellValue(p.getCategoria());
                fila.createCell(4).setCellValue(p.getPrecio().doubleValue());
                fila.createCell(5).setCellValue(p.getStock());
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
        List<Producto> productos = productoRepository.findByEstadoTrue();

        // 1. Leer el archivo HTML del template
        ClassPathResource resource = new ClassPathResource("templates/productos-reporte.html");
        String html;
        try (InputStream is = resource.getInputStream()) {
            html = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }

        // 2. Generar dinámicamente las filas de productos en formato HTML
        StringBuilder rowsBuilder = new StringBuilder();
        for (Producto p : productos) {
            rowsBuilder.append("<tr>")
                    .append("<td>").append(p.getIdProducto()).append("</td>")
                    .append("<td>").append(p.getNombre()).append("</td>")
                    .append("<td>").append(p.getDescripcion() != null ? p.getDescripcion() : "").append("</td>")
                    .append("<td>").append(p.getCategoria()).append("</td>")
                    .append("<td>S/. ").append(p.getPrecio().setScale(2)).append("</td>")
                    .append("<td>").append(p.getStock()).append("</td>")
                    .append("</tr>");
        }

        // 3. Reemplazar el marcador en el HTML
        html = html.replace("{{ROWS}}", rowsBuilder.toString());

        // 4. Convertir HTML a PDF usando HTMLWorker de iText
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4);
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
