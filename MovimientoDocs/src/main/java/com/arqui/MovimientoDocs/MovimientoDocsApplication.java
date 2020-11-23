package com.arqui.MovimientoDocs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class MovimientoDocsApplication implements CommandLineRunner {

    final static Logger LOG = LoggerFactory.getLogger(MovimientoDocsApplication.class);

    public MovimientoDocsApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(MovimientoDocsApplication.class, args);
    }

    @Value("${com.ruta.entrada}")
    private String entrada;

    @Value("${com.ruta.salida}")
    private String salida;

    @Value("${com.tiempo}")
    private String tiempo;
    
    @Value("${com.letra}")
    private String letra;


    @Override
    public void run(String... args) {
        String sDirectorio = entrada;
        File f = new File(sDirectorio);

        if (f.exists()) {
            LOG.info("Directorio Existe");
            File[] ficheros = f.listFiles();
            for (File fichero : ficheros) {
                BasicFileAttributes attrs;
                try {
                    attrs = Files.readAttributes(fichero.toPath(), BasicFileAttributes.class);
                    FileTime time = attrs.lastModifiedTime();
                    
                    String pattern = "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String formatted = simpleDateFormat.format(new Date(time.toMillis()));
                    LOG.info("Documento " + fichero.getName() + " " + formatted + " Diferendia de " + minutesDiff(GetItemDate(formatted), GetItemDate(getCurrentTimeStamp())) + " Min Con la hora actual " + getCurrentTimeStamp());

                    if (minutesDiff(GetItemDate(formatted), GetItemDate(getCurrentTimeStamp())) > Integer.parseInt(tiempo) && fichero.getName().substring(0,1).equals(letra)) {
                        LOG.info("DOcumento se mueve");
                        //Original file
                        File dataInputFile = new File(fichero.getAbsolutePath());
                        //New path
                        File fileSendPath = new File(salida, dataInputFile.getName());
                        //Moving the file.
                        dataInputFile.renameTo(fileSendPath);

                    } else {
                        LOG.info("Documento no se mueve por que el tiempo es menor al establecido o su nombre no empieza por la letra " + letra);
                    }
                } catch (Exception e) {
                }
            }
        } else {

        }
    }

    //int diff = minutesDiff(GetItemDate("11/21/2011 7:00:00 AM"), GetItemDate("11/21/2011 1:00:00 PM"));
    public static Date GetItemDate(final String date) {
        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setCalendar(cal);

        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static int minutesDiff(Date earlierDate, Date laterDate) {
        if (earlierDate == null || laterDate == null) {
            return 0;
        }

        return (int) ((laterDate.getTime() / 60000) - (earlierDate.getTime() / 60000));
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }

}
