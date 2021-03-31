package com.quavii.dsige.lectura.helper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.exifinterface.media.ExifInterface;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.quavii.dsige.lectura.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class HelperDialog {

    public static final String FolderImg = "Dsige/Veritas";
    private static final int img_height_default = 800;
    private static final int img_width_default = 600;

    private static AlertDialog dialog;

    public static void MensajeOk(Context context, String titulo, final String m) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));
        @SuppressLint("InflateParams") View v = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        TextView textViewMessage = v.findViewById(R.id.textViewMessage);
        TextView textViewTitle = v.findViewById(R.id.textViewTitle);
        Button buttonCancelar = v.findViewById(R.id.buttonCancelar);
        buttonCancelar.setVisibility(View.GONE);
        Button buttonAceptar = v.findViewById(R.id.buttonAceptar);
        textViewTitle.setText(titulo);
        textViewMessage.setTextSize(18);
        textViewMessage.setText(m);
        buttonAceptar.setOnClickListener(view -> {
            dialog.dismiss();
//            if (m.equals("Actualizar Versión del Aplicativo.")) {
//                String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
//                try {
//                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                }
//                dialog.dismiss();
//            } else {
//                dialog.dismiss();
//            }
        });
        builder.setView(v);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static String getFechaActual() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    static String getDateFirmReconexiones(int id, int tipo, String f) {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HHmmssSSS");
        String fechaActual = format.format(date);
        return String.format("Firm(%s)_%s_%s_%s.jpg", f, id, tipo, fechaActual);
    }

    static String getDateFirmReparto(int id, int tipo) {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HHmmssSSS");
        String fechaActual = format.format(date);
        return String.format("Firm_%s_%s_%s.jpg", id, tipo, fechaActual);
    }

    public static File getFolder(Context context) {
        File folder = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath());
//        File folder = new File(Environment.getExternalStorageDirectory(), FolderImg);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                Log.i("TAG", "FOLDER CREADO");
            }
        }
        return folder;
    }

    static void comprimirImagen(String PathFile) {
        Bitmap imagen = ShrinkBitmap(PathFile);
        CopyBitmatToFile(PathFile, imagen);
    }

    private static String getDateTimeFormatString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a");
        return df.format(date);
    }

    private static Bitmap ProcessingBitmap_SetDATETIME(Bitmap bm1, String captionString) {
        //Bitmap bm1 = null;
        Bitmap newBitmap = null;
        try {

            Bitmap.Config config = bm1.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);

            Canvas newCanvas = new Canvas(newBitmap);
            newCanvas.drawBitmap(bm1, 0, 0, null);

            if (captionString != null) {

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.RED);
                paintText.setTextSize(22);
                paintText.setStyle(Paint.Style.FILL);
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString, 0, captionString.length(), rectText);
                newCanvas.drawText(captionString, 0, rectText.height(), paintText);
            }

            //} catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newBitmap;
    }

    private static void CopyBitmatToFile(String filename, Bitmap bitmap) {
        try {
            File f = new File(filename);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    private static Bitmap ShrinkBitmap(String file) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inJustDecodeBounds = true;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) HelperDialog.img_height_default);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) HelperDialog.img_width_default);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(file, options);

    }

    private static void ShrinkBitmapOnlyReduce(String file, String captionString) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inJustDecodeBounds = true;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) HelperDialog.img_height_default);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) HelperDialog.img_width_default);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }

        options.inJustDecodeBounds = false;

        try {


            Bitmap b = BitmapFactory.decodeFile(file, options);

            Bitmap.Config config = b.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            Bitmap newBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), config);

            Canvas newCanvas = new Canvas(newBitmap);
            newCanvas.drawBitmap(b, 0, 0, null);

            if (captionString != null) {

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.RED);
                paintText.setTextSize(22);
                paintText.setStyle(Paint.Style.FILL);
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString, 0, captionString.length(), rectText);
                newCanvas.drawText(captionString, 0, rectText.height(), paintText);
            }

            FileOutputStream fOut = new FileOutputStream(file);
            String imageName = file.substring(file.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(file);
            if (imageType.equalsIgnoreCase("png")) {
                newBitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            }
            fOut.flush();
            fOut.close();
            newBitmap.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // TODO SOBRE ROTAR LA PHOTO

    private static String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree, photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }

    private static String rotateImage(int degree, String imagePath) {

        if (degree <= 0) {
            ShrinkBitmapOnlyReduce(imagePath, getDateTimeFormatString(new Date(new File(imagePath).lastModified())));
            return imagePath;
        }
        try {

            Bitmap b = ShrinkBitmap(imagePath);
            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                b = ProcessingBitmap_SetDATETIME(b, getDateTimeFormatString(new Date(new File(imagePath).lastModified())));
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 70, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 70, out);
            }
            fOut.flush();
            fOut.close();
            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }
}