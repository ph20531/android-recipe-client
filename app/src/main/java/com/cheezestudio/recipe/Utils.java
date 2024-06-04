package com.cheezestudio.recipe;

import android.content.Context;
import android.text.Html;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Utils {
    public static ArrayList<HashMap<String, String>> prettifyJson(Context context, JSONObject jsonObject, int indentSpaces) {
        try {
            ArrayList<HashMap<String, String>> data = new ArrayList<>();
            String jsonStr = colorizeJson(context, jsonObject, indentSpaces);
            String[] lines = jsonStr.split("<br/>");

            for (int i = 0; i < lines.length; i++) {
                HashMap<String, String> item = new HashMap<>();
                item.put("line_number", formatLineNumber(i));
                item.put("line_data", lines[i]);
                data.add(item);
            }
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String colorizeJson(Context context, JSONObject jsonObject, int indentSpaces) throws JSONException {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<pre><code>");
        processJsonValue(context, jsonObject, htmlBuilder, indentSpaces, 0);
        htmlBuilder.append("</code></pre>");
        return htmlBuilder.toString();
    }

    private static void processJsonValue(Context context, Object value, StringBuilder htmlBuilder, int indentSpaces, int indentLevel) throws JSONException {
        String defaultIndent = "&nbsp;".repeat(indentSpaces);
        String indent = defaultIndent.repeat(indentLevel);

        String keyColor = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.colorkey)));
        String valueColor = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.colorValue)));

        if (value instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) value;
            htmlBuilder.append("{<br/>");
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object nestedValue = jsonObject.get(key);
                String keyHtml = "<span style=\"color:" + keyColor + ";\">" + key + "</span>";
                htmlBuilder.append(indent).append(defaultIndent).append(keyHtml).append(": ");
                processJsonValue(context, nestedValue, htmlBuilder, indentSpaces, indentLevel + 1);
                if (keys.hasNext()) {
                    htmlBuilder.append(",");
                }
                htmlBuilder.append("<br/>");
            }
            htmlBuilder.append(indent).append("}");
        } else if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            htmlBuilder.append("[<br/>");
            for (int i = 0; i < jsonArray.length(); i++) {
                htmlBuilder.append(indent).append(defaultIndent);
                processJsonValue(context, jsonArray.get(i), htmlBuilder, indentSpaces, indentLevel + 1);
                if (i < jsonArray.length() - 1) {
                    htmlBuilder.append(",");
                }
                htmlBuilder.append("<br/>");
            }
            htmlBuilder.append(indent).append("]");
        } else if (value instanceof String) {
            String valueHtml = "<span style=\"color:" + valueColor + ";\">\"" + value + "\"</span>";
            htmlBuilder.append(valueHtml);
        } else {
            String valueHtml = "<span style=\"color:" + valueColor + ";\">" + value.toString() + "</span>";
            htmlBuilder.append(valueHtml);
        }
    }

    public static String formatLineNumber(int index) {
        return String.format("%5d", index + 1);
    }

    public static String removeHtmlTags(String htmlString) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY).toString().trim();
        } else {
            return Html.fromHtml(htmlString).toString().trim();
        }
    }
}
