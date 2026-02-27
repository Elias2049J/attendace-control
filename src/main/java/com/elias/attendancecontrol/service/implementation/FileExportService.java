package com.elias.attendancecontrol.service.implementation;

import com.elias.attendancecontrol.model.dto.ActivityAttendanceSummaryDTO;
import com.elias.attendancecontrol.model.dto.ActivityUserReportDTO;
import com.elias.attendancecontrol.model.dto.UserActivityReportDTO;
import com.elias.attendancecontrol.service.ExportService;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileExportService implements ExportService {

    private static final BaseColor PDF_HEADER_BG    = new BaseColor(30, 58, 95);
    private static final BaseColor PDF_SUBHEADER_BG = new BaseColor(52, 96, 152);
    private static final BaseColor PDF_ROW_ALT      = new BaseColor(235, 241, 250);
    private static final BaseColor PDF_TOTAL_BG     = new BaseColor(220, 230, 245);
    private static final BaseColor PDF_GREEN        = new BaseColor(39, 174, 96);
    private static final BaseColor PDF_YELLOW       = new BaseColor(230, 162, 0);
    private static final BaseColor PDF_RED          = new BaseColor(192, 57, 43);

    private static final Font FONT_TITLE    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE);
    private static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE);
    private static final Font FONT_HEADER   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
    private static final Font FONT_CELL     = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
    private static final Font FONT_TOTAL    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new BaseColor(30, 58, 95));
    private static final Font FONT_META     = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private byte[] generatePdf(Map<String, Object> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 50, 40);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter w, Document d) {
                    PdfContentByte cb = w.getDirectContent();
                    cb.setColorFill(PDF_HEADER_BG);
                    cb.rectangle(d.left(), d.bottom() - 15, d.right() - d.left(), 12);
                    cb.fill();
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                            new Phrase("Página " + w.getPageNumber(), FONT_META),
                            (d.right() + d.left()) / 2f, d.bottom() - 10, 0);
                }
            });

            doc.open();

            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            PdfPCell titleCell = new PdfPCell(new Phrase("REPORTE DE ASISTENCIAS", FONT_TITLE));
            titleCell.setBackgroundColor(PDF_HEADER_BG);
            titleCell.setPadding(12);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.addCell(titleCell);

            String metaText = "Generado: " + LocalDateTime.now().format(DT_FMT);
            if (data.containsKey("startDate") && data.containsKey("endDate")) {
                metaText += "   |   Período: " + data.get("startDate") + " al " + data.get("endDate");
            }
            PdfPCell metaCell = new PdfPCell(new Phrase(metaText, FONT_SUBTITLE));
            metaCell.setBackgroundColor(PDF_SUBHEADER_BG);
            metaCell.setPadding(6);
            metaCell.setBorder(Rectangle.NO_BORDER);
            metaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.addCell(metaCell);

            doc.add(header);
            doc.add(Chunk.NEWLINE);

            List<?> details = (List<?>) data.get("details");
            if (details == null || details.isEmpty()) {
                doc.add(new Paragraph("Sin datos para mostrar.", FONT_CELL));
                doc.close();
                return baos.toByteArray();
            }

            boolean isGeneral = details.getFirst() instanceof ActivityAttendanceSummaryDTO;
            PdfPTable table = isGeneral ? buildGeneralPdfTable(details) : buildDetailPdfTable(details);
            doc.add(table);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF report", e);
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    private PdfPTable buildGeneralPdfTable(List<?> details) throws DocumentException {
        String[] headers = {"Actividad", "Sesiones", "Presentes", "Faltas", "Tardanzas", "% Asistencia", "Indicador"};
        float[] widths = {30f, 10f, 10f, 10f, 10f, 12f, 18f};

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingBefore(4);

        for (String h : headers) table.addCell(headerCell(h));

        long totalSesiones = 0, totalPresentes = 0, totalFaltas = 0, totalTardanzas = 0;
        int row = 0;
        for (Object obj : details) {
            if (!(obj instanceof ActivityAttendanceSummaryDTO dto)) continue;
            BaseColor bg = (row % 2 == 0) ? BaseColor.WHITE : PDF_ROW_ALT;

            table.addCell(dataCell(dto.activityName(), bg, Element.ALIGN_LEFT));
            table.addCell(dataCell(dto.totalSesiones().toString(), bg, Element.ALIGN_CENTER));
            table.addCell(dataCell(dto.totalPresentes().toString(), bg, Element.ALIGN_CENTER));
            table.addCell(dataCell(dto.totalFaltas().toString(), bg, Element.ALIGN_CENTER));
            table.addCell(dataCell(dto.totalTardanzas().toString(), bg, Element.ALIGN_CENTER));

            double pct = dto.porcentajeAsistencia() != null ? dto.porcentajeAsistencia() * 100 : 0.0;
            table.addCell(percentCell(pct, bg));
            table.addCell(barCell(pct, bg));

            totalSesiones  += dto.totalSesiones();
            totalPresentes += dto.totalPresentes();
            totalFaltas    += dto.totalFaltas();
            totalTardanzas += dto.totalTardanzas();
            row++;
        }

        double totalPct = totalSesiones > 0 ? (totalPresentes * 100.0 / totalSesiones) : 0.0;
        table.addCell(totalCell("TOTALES", Element.ALIGN_LEFT));
        table.addCell(totalCell(String.valueOf(totalSesiones), Element.ALIGN_CENTER));
        table.addCell(totalCell(String.valueOf(totalPresentes), Element.ALIGN_CENTER));
        table.addCell(totalCell(String.valueOf(totalFaltas), Element.ALIGN_CENTER));
        table.addCell(totalCell(String.valueOf(totalTardanzas), Element.ALIGN_CENTER));
        table.addCell(totalCell(String.format("%.1f%%", totalPct), Element.ALIGN_CENTER));
        table.addCell(totalCell("", Element.ALIGN_CENTER));

        return table;
    }

    private PdfPTable buildDetailPdfTable(List<?> details) throws DocumentException {
        String[] headers = {"Actividad", "Participante", "Sesiones", "Presentes", "Faltas", "Tardanzas", "% Asistencia", "Indicador"};
        float[] widths = {24f, 20f, 8f, 8f, 8f, 9f, 10f, 13f};

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingBefore(4);

        for (String h : headers) table.addCell(headerCell(h));

        int row = 0;
        for (Object obj : details) {
            String actividad, usuario;
            long sesiones, presentes, faltas, tardanzas;

            if (obj instanceof ActivityUserReportDTO dto) {
                actividad = dto.activityName(); usuario = dto.userName();
                sesiones = dto.totalSesiones(); presentes = dto.totalPresentes();
                faltas = dto.totalFaltas(); tardanzas = dto.totalTardanzas();
            } else if (obj instanceof UserActivityReportDTO dto) {
                actividad = dto.activityName(); usuario = dto.userName();
                sesiones = dto.totalSesiones(); presentes = dto.totalPresentes();
                faltas = dto.totalFaltas(); tardanzas = dto.totalTardanzas();
            } else {
                continue;
            }

            double pct = sesiones > 0 ? (presentes * 100.0 / sesiones) : 0.0;
            BaseColor bg = (row % 2 == 0) ? BaseColor.WHITE : PDF_ROW_ALT;

            table.addCell(dataCell(actividad, bg, Element.ALIGN_LEFT));
            table.addCell(dataCell(usuario, bg, Element.ALIGN_LEFT));
            table.addCell(dataCell(String.valueOf(sesiones), bg, Element.ALIGN_CENTER));
            table.addCell(dataCell(String.valueOf(presentes), bg, Element.ALIGN_CENTER));
            table.addCell(dataCell(String.valueOf(faltas), bg, Element.ALIGN_CENTER));
            table.addCell(dataCell(String.valueOf(tardanzas), bg, Element.ALIGN_CENTER));
            table.addCell(percentCell(pct, bg));
            table.addCell(barCell(pct, bg));
            row++;
        }

        return table;
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_HEADER));
        cell.setBackgroundColor(PDF_HEADER_BG);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(PDF_SUBHEADER_BG);
        return cell;
    }

    private PdfPCell dataCell(String text, BaseColor bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_CELL));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(new BaseColor(200, 210, 225));
        return cell;
    }

    private PdfPCell percentCell(double pct, BaseColor bg) {
        BaseColor color = pct >= 80 ? PDF_GREEN : (pct >= 60 ? PDF_YELLOW : PDF_RED);
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, color);
        PdfPCell cell = new PdfPCell(new Phrase(String.format("%.1f%%", pct), f));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(new BaseColor(200, 210, 225));
        return cell;
    }

    private PdfPCell barCell(double pct, BaseColor bg) {
        int filled = (int) Math.round(pct / 10);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) bar.append(i < filled ? "█" : "░");
        BaseColor color = pct >= 80 ? PDF_GREEN : (pct >= 60 ? PDF_YELLOW : PDF_RED);
        Font f = FontFactory.getFont(FontFactory.COURIER, 8, color);
        PdfPCell cell = new PdfPCell(new Phrase(bar.toString(), f));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(new BaseColor(200, 210, 225));
        return cell;
    }

    private PdfPCell totalCell(String text, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_TOTAL));
        cell.setBackgroundColor(PDF_TOTAL_BG);
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(PDF_SUBHEADER_BG);
        return cell;
    }

    private byte[] generateExcel(Map<String, Object> data) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            XSSFSheet sheet = wb.createSheet("Reporte de Asistencias");

            XSSFCellStyle titleStyle      = buildTitleStyle(wb);
            XSSFCellStyle metaStyle       = buildMetaStyle(wb);
            XSSFCellStyle headerStyle     = buildHeaderStyle(wb);
            XSSFCellStyle dataStyle       = buildDataStyle(wb, false, false);
            XSSFCellStyle dataAltStyle    = buildDataStyle(wb, true, false);
            XSSFCellStyle dataCtrStyle    = buildDataStyle(wb, false, true);
            XSSFCellStyle dataAltCtrStyle = buildDataStyle(wb, true, true);
            XSSFCellStyle totalStyle      = buildTotalStyle(wb, false);
            XSSFCellStyle totalCtrStyle   = buildTotalStyle(wb, true);
            XSSFCellStyle greenStyle      = buildPctStyle(wb, false, new XSSFColor(new byte[]{(byte)39,(byte)174,(byte)96}, null));
            XSSFCellStyle yellowStyle     = buildPctStyle(wb, false, new XSSFColor(new byte[]{(byte)180,(byte)120,(byte)0}, null));
            XSSFCellStyle redStyle        = buildPctStyle(wb, false, new XSSFColor(new byte[]{(byte)192,(byte)57,(byte)43}, null));
            XSSFCellStyle greenAltStyle   = buildPctStyle(wb, true,  new XSSFColor(new byte[]{(byte)39,(byte)174,(byte)96}, null));
            XSSFCellStyle yellowAltStyle  = buildPctStyle(wb, true,  new XSSFColor(new byte[]{(byte)180,(byte)120,(byte)0}, null));
            XSSFCellStyle redAltStyle     = buildPctStyle(wb, true,  new XSSFColor(new byte[]{(byte)192,(byte)57,(byte)43}, null));

            List<?> details = (List<?>) data.get("details");
            boolean isGeneral = details != null && !details.isEmpty()
                    && details.getFirst() instanceof ActivityAttendanceSummaryDTO;

            String[] headers = isGeneral
                    ? new String[]{"Actividad", "Total Sesiones", "Presentes", "Faltas", "Tardanzas", "% Asistencia"}
                    : new String[]{"Actividad", "Participante", "Total Sesiones", "Presentes", "Faltas", "Tardanzas", "% Asistencia"};
            int cols = headers.length;

            int rowIdx = 0;
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(28);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE ASISTENCIAS - MIRAE");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, cols - 1));

            String metaText = "Generado: " + LocalDateTime.now().format(DT_FMT);
            if (data.containsKey("startDate") && data.containsKey("endDate")) {
                metaText += "   |   Período: " + data.get("startDate") + " al " + data.get("endDate");
            }
            Row metaRow = sheet.createRow(rowIdx++);
            metaRow.setHeightInPoints(18);
            Cell metaCell = metaRow.createCell(0);
            metaCell.setCellValue(metaText);
            metaCell.setCellStyle(metaStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, cols - 1));

            rowIdx++;

            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            if (details == null || details.isEmpty()) {
                wb.write(baos);
                return baos.toByteArray();
            }

            long totalSesiones = 0, totalPresentes = 0, totalFaltas = 0, totalTardanzas = 0;
            int dataRow = 0;

            for (Object obj : details) {
                boolean alt = (dataRow % 2 != 0);
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(18);

                if (isGeneral && obj instanceof ActivityAttendanceSummaryDTO dto) {
                    row.createCell(0).setCellValue(dto.activityName());
                    row.getCell(0).setCellStyle(alt ? dataAltStyle : dataStyle);
                    setCenterLong(row, 1, dto.totalSesiones(),  alt ? dataAltCtrStyle : dataCtrStyle);
                    setCenterLong(row, 2, dto.totalPresentes(), alt ? dataAltCtrStyle : dataCtrStyle);
                    setCenterLong(row, 3, dto.totalFaltas(),    alt ? dataAltCtrStyle : dataCtrStyle);
                    setCenterLong(row, 4, dto.totalTardanzas(), alt ? dataAltCtrStyle : dataCtrStyle);
                    double pct = dto.porcentajeAsistencia() != null ? dto.porcentajeAsistencia() * 100 : 0.0;
                    Cell pctCell = row.createCell(5);
                    pctCell.setCellValue(pct / 100.0);
                    pctCell.setCellStyle(pctStyle(pct, alt, greenStyle, greenAltStyle, yellowStyle, yellowAltStyle, redStyle, redAltStyle));
                    totalSesiones  += dto.totalSesiones();
                    totalPresentes += dto.totalPresentes();
                    totalFaltas    += dto.totalFaltas();
                    totalTardanzas += dto.totalTardanzas();

                } else {
                    String actividad, usuario;
                    long sesiones, presentes, faltas, tardanzas;

                    if (obj instanceof ActivityUserReportDTO dto) {
                        actividad = dto.activityName(); usuario = dto.userName();
                        sesiones = dto.totalSesiones(); presentes = dto.totalPresentes();
                        faltas = dto.totalFaltas(); tardanzas = dto.totalTardanzas();
                    } else if (obj instanceof UserActivityReportDTO dto) {
                        actividad = dto.activityName(); usuario = dto.userName();
                        sesiones = dto.totalSesiones(); presentes = dto.totalPresentes();
                        faltas = dto.totalFaltas(); tardanzas = dto.totalTardanzas();
                    } else {
                        continue;
                    }

                    double pct = sesiones > 0 ? (presentes * 100.0 / sesiones) : 0.0;
                    row.createCell(0).setCellValue(actividad);
                    row.getCell(0).setCellStyle(alt ? dataAltStyle : dataStyle);
                    row.createCell(1).setCellValue(usuario);
                    row.getCell(1).setCellStyle(alt ? dataAltStyle : dataStyle);
                    setCenterLong(row, 2, sesiones,  alt ? dataAltCtrStyle : dataCtrStyle);
                    setCenterLong(row, 3, presentes, alt ? dataAltCtrStyle : dataCtrStyle);
                    setCenterLong(row, 4, faltas,    alt ? dataAltCtrStyle : dataCtrStyle);
                    setCenterLong(row, 5, tardanzas, alt ? dataAltCtrStyle : dataCtrStyle);
                    Cell pctCell = row.createCell(6);
                    pctCell.setCellValue(pct / 100.0);
                    pctCell.setCellStyle(pctStyle(pct, alt, greenStyle, greenAltStyle, yellowStyle, yellowAltStyle, redStyle, redAltStyle));
                    totalSesiones  += sesiones;
                    totalPresentes += presentes;
                    totalFaltas    += faltas;
                    totalTardanzas += tardanzas;
                }
                dataRow++;
            }

            Row totalRow = sheet.createRow(rowIdx);
            totalRow.setHeightInPoints(20);
            int tc = 0;
            Cell totalLabel = totalRow.createCell(tc++);
            totalLabel.setCellValue("TOTALES");
            totalLabel.setCellStyle(totalStyle);
            if (!isGeneral) {
                Cell empty = totalRow.createCell(tc++);
                empty.setCellStyle(totalStyle);
            }
            setCenterLong(totalRow, tc++, totalSesiones,  totalCtrStyle);
            setCenterLong(totalRow, tc++, totalPresentes, totalCtrStyle);
            setCenterLong(totalRow, tc++, totalFaltas,    totalCtrStyle);
            setCenterLong(totalRow, tc++, totalTardanzas, totalCtrStyle);
            double globalPct = totalSesiones > 0 ? (totalPresentes * 100.0 / totalSesiones) : 0.0;
            Cell globalPctCell = totalRow.createCell(tc);
            globalPctCell.setCellValue(globalPct / 100.0);
            globalPctCell.setCellStyle(totalCtrStyle);

            short pctFmt = wb.createDataFormat().getFormat("0.0%");
            int pctColIdx = isGeneral ? 5 : 6;
            for (int r = 4; r <= rowIdx; r++) {
                Row rr = sheet.getRow(r);
                if (rr == null) continue;
                Cell c = rr.getCell(pctColIdx);
                if (c != null && c.getCellType() == CellType.NUMERIC) {
                    XSSFCellStyle s = wb.createCellStyle();
                    s.cloneStyleFrom(c.getCellStyle());
                    s.setDataFormat(pctFmt);
                    c.setCellStyle(s);
                }
            }

            sheet.setColumnWidth(0, 38 * 256);
            if (!isGeneral) sheet.setColumnWidth(1, 30 * 256);
            for (int i = isGeneral ? 1 : 2; i < cols; i++) sheet.setColumnWidth(i, 14 * 256);

            sheet.createFreezePane(0, 4);
            sheet.setAutoFilter(new CellRangeAddress(3, 3, 0, cols - 1));

            wb.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating Excel report", e);
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
    }

    private XSSFCellStyle buildTitleStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30,(byte)58,(byte)95}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short)14);
        f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
        f.setFontName("Calibri");
        s.setFont(f);
        return s;
    }

    private XSSFCellStyle buildMetaStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)52,(byte)96,(byte)152}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont();
        f.setFontHeightInPoints((short)10);
        f.setColor(new XSSFColor(new byte[]{(byte)220,(byte)230,(byte)255}, null));
        f.setFontName("Calibri");
        s.setFont(f);
        return s;
    }

    private XSSFCellStyle buildHeaderStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30,(byte)58,(byte)95}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.MEDIUM);
        s.setBottomBorderColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null).getIndex());
        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short)11);
        f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
        f.setFontName("Calibri");
        s.setFont(f);
        return s;
    }

    private XSSFCellStyle buildDataStyle(XSSFWorkbook wb, boolean alt, boolean center) {
        XSSFCellStyle s = wb.createCellStyle();
        if (alt) {
            s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)235,(byte)241,(byte)250}, null));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        s.setAlignment(center ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBottomBorderColor(new XSSFColor(new byte[]{(byte)200,(byte)210,(byte)225}, null).getIndex());
        XSSFFont f = wb.createFont();
        f.setFontHeightInPoints((short)10);
        f.setFontName("Calibri");
        s.setFont(f);
        return s;
    }

    private XSSFCellStyle buildTotalStyle(XSSFWorkbook wb, boolean center) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)220,(byte)230,(byte)245}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(center ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderTop(BorderStyle.MEDIUM);
        s.setTopBorderColor(new XSSFColor(new byte[]{(byte)30,(byte)58,(byte)95}, null).getIndex());
        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short)10);
        f.setColor(new XSSFColor(new byte[]{(byte)30,(byte)58,(byte)95}, null));
        f.setFontName("Calibri");
        s.setFont(f);
        return s;
    }

    private XSSFCellStyle buildPctStyle(XSSFWorkbook wb, boolean alt, XSSFColor color) {
        XSSFCellStyle s = wb.createCellStyle();
        if (alt) {
            s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)235,(byte)241,(byte)250}, null));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBottomBorderColor(new XSSFColor(new byte[]{(byte)200,(byte)210,(byte)225}, null).getIndex());
        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short)10);
        f.setColor(color);
        f.setFontName("Calibri");
        s.setFont(f);
        return s;
    }

    private XSSFCellStyle pctStyle(double pct, boolean alt,
                                    XSSFCellStyle g, XSSFCellStyle ga,
                                    XSSFCellStyle y, XSSFCellStyle ya,
                                    XSSFCellStyle r, XSSFCellStyle ra) {
        if (pct >= 80) return alt ? ga : g;
        if (pct >= 60) return alt ? ya : y;
        return alt ? ra : r;
    }

    private void setCenterLong(Row row, int col, long value, XSSFCellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    @Override
    public byte[] exportReport(Map<String, Object> data, String format) {
        return switch (format) {
            case null    -> throw new IllegalArgumentException("El formato no puede ser nulo");
            case "pdf"   -> generatePdf(data);
            case "excel" -> generateExcel(data);
            default      -> throw new IllegalArgumentException("Formato no soportado: " + format);
        };
    }
}
