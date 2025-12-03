package com.yapily.orione2e.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellUtils
{
    @SuppressWarnings("unused")
    public static String readProcessOutput(Process p)
    {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream())))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
        catch(Exception e)
        {
            return "";
        }
    }
}
