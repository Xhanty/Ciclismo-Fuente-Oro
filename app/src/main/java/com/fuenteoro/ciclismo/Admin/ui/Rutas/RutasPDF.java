package com.fuenteoro.ciclismo.Admin.ui.Rutas;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.fuenteoro.ciclismo.BuildConfig;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class RutasPDF {
    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubtitle = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.RED);

    public RutasPDF(Context context) {
        this.context = context;
    }

    public void openDocument(){
        createFile();
        try {
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

        } catch (Exception e){
            Log.e("openDocument", e.toString());
        }
    }

    private void createFile(){
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF");
        if(!folder.exists())
            folder.mkdirs();
        pdfFile = new File(folder, "Reporte_Rutas.pdf");
    }

    public void closeDocument(){
        document.close();
    }

    public void addInfo(){
        try{
            paragraph = new Paragraph();
            addChild(new Paragraph("Listado de rutas", fTitle));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);

        } catch (Exception e){
            Log.e("addInfo", e.toString());
        }

    }

    private void addChild(Paragraph childParagraph){
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void createTable(String[]header, ArrayList<String[]> sitios, String fecha){
        try {
            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setSpacingBefore(5);
            pdfPTable.setSpacingAfter(5);
            PdfPCell pdfPCell;

            int indexC = 0;
            while (indexC < header.length) {
                pdfPCell = new PdfPCell(new Phrase(header[indexC++], fSubtitle));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBackgroundColor(BaseColor.GREEN);
                pdfPTable.addCell(pdfPCell);
            }

            for (int indexR = 0; indexR < sitios.size(); indexR++) {
                String[] row = sitios.get(indexR);
                for (indexC = 0; indexC < header.length; indexC++) {
                    pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setFixedHeight(40);
                    pdfPTable.addCell(pdfPCell);
                }
            }

            paragraph.add(pdfPTable);
            addChild(new Paragraph("Generado: " + fecha , fHighText));
            document.add(paragraph);

        } catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            Log.e("createTable", e.toString());
        }
    }

    public void viewPDF(Activity activity){
        if(pdfFile.exists()){
            Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/pdf");
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e){
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                Toast.makeText(activity.getApplicationContext(), "No cuentas con una aplicación para visualizar el PDF", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity.getApplicationContext(), "El archivo no se encontró", Toast.LENGTH_LONG).show();
        }
    }
}
