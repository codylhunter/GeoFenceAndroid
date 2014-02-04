/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package capsrock.beta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import capsrock.beta.Structures.WebTimeEntry;
import android.os.AsyncTask;

public class sendToServer extends AsyncTask<WebTimeEntry, Void, Boolean>
{
    @SuppressWarnings("deprecation")
	@Override
    protected Boolean doInBackground(WebTimeEntry... entry)
    {
        URL url;
        //String connectionStr = "http://capsrock.csc.calpoly.edu/timeEntry/api/activity/"; 
        String connectionStr = "http://129.65.148.31:8000/api/activity/"; 
        try
        {
            //Set up our connection
            url = new URL(connectionStr);
            Integer month = entry[0].date.getTime().getMonth() + 1;
            String monthString;
            if(month < 10)
                monthString = "0" + month;
            else
                monthString = month.toString();
            int year = entry[0].date.getTime().getYear() + 1900;

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            //connection.setRequestProperty("Content-Type", "application/json");

            //Construct string to be sent
            String input = "{\"location\": \"" + entry[0].location + "\", "
                             + "\"date\": \"" + year + "-" + monthString + "-" + entry[0].date.getTime().getDate() + "\", "
                             + "\"startTime\": \"" + entry[0].te.startTime.getTime().getHours() + ":" + entry[0].te.startTime.getTime().getMinutes() + ":" + entry[0].te.startTime.getTime().getSeconds() + "\", "
                             + "\"stopTime\": \"" + entry[0].te.endTime.getTime().getHours() + ":" + entry[0].te.endTime.getTime().getMinutes() + ":" + entry[0].te.endTime.getTime().getSeconds() + "\", "
                             + "\"workTime\": \"" + entry[0].te.workTime + "\""
                             + "}";
            //Send string to server
            System.out.println(input);
            OutputStream out = connection.getOutputStream();
            out.write(input.getBytes());
            out.flush();

            //Verify that there was no error
            if(connection.getResponseCode() != HttpURLConnection.HTTP_CREATED)
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());

            
            //Read what was sent to us
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String output;
            while ((output = br.readLine()) != null)
            {
                System.out.println(output);
            }               

            //Exit the connection
            connection.disconnect();
        } 
        //Error catching
        catch (MalformedURLException ex)
        {
            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
} 
