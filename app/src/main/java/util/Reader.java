package util;

import java.io.*;

import android.content.Context;

public class Reader {
    private StringBuilder shader;
    private boolean error;

    public String readTextFileFromRawResource(final Context context, final int resourceId) {
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        //String ultimoCaracter = "";
        shader = new StringBuilder();

        try {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                shader.append(nextLine);
                shader.append(" ");
                /*ultimoCaracter = shader.substring(shader.length()-1);
                if (!ultimoCaracter.equals("}") && !ultimoCaracter.equals(" ")) shader.append(" \" + \" ");
                    else shader.append(" ");*/
            }
        }
        catch (IOException e) {
            error = true;
            throw new IllegalArgumentException("No se pudo leer el archivo", e);
        }

        return error ? null : shader.toString();
    }

}

