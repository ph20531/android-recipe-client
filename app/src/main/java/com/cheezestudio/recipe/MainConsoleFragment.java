package com.cheezestudio.recipe;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cheezestudio.recipe.APIClient.APIRequest;
import com.cheezestudio.recipe.APIClient.HttpMethod;
import com.cheezestudio.recipe.APIClient.OnAPIResponseListener;
import com.cheezestudio.recipe.APIClient.RequestType;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainConsoleFragment extends Fragment {
    private MainActivity context;
    private ListView jsonLogList;
    private JsonLogListAdapter jsonLogListAdapter;
    private Button request;
    private ProgressBar circularProgress;
    private TextView elapsedTime;
    private int executionModesId = R.id.sync;
    private ArrayList<HashMap<String, String>> log = new ArrayList<>();
    private long startTime, endTime;
    private Handler handler = new Handler();
    private Runnable elapsedTimeRunnable;
    private long delayMillis = 5000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = ((MainActivity) requireActivity());

        View view = inflater.inflate(R.layout.fragment_console_main, container, false);

        RadioGroup radioGroup = view.findViewById(R.id.execution_modes);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            executionModesId = checkedId;
        });
        request = view.findViewById(R.id.request);
        request.setOnClickListener(v -> {
            if (executionModesId == R.id.sync) syncAPIResponse();
            else // R.id.async
                asyncAPIResponse();
        });

        circularProgress = requireActivity().findViewById(R.id.circular_progress);
        elapsedTime = requireActivity().findViewById(R.id.elapsed_time);

        jsonLogList = view.findViewById(R.id.json_log_list);
        jsonLogList.setEmptyView(view.findViewById(R.id.empty));
        jsonLogList.setOnItemClickListener((parent, logView, position, id) -> {
            HashMap<String, String> item = log.get(position);

            String plainText;
            if (item.containsKey("log")) plainText = item.getOrDefault("log", "");
            else plainText = item.getOrDefault("line_data", "");

            plainText = Utils.removeHtmlTags(plainText);

            ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("clipBoard", plainText);
            clipboardManager.setPrimaryClip(clipData);

            Snackbar.make(logView, "클립보드에 복사되었습니다.", Snackbar.LENGTH_SHORT).show();
        });

        jsonLogListAdapter = new JsonLogListAdapter(requireContext(), log);
        jsonLogList.setAdapter(jsonLogListAdapter);

        return view;
    }

    private boolean lazyUpdate = false;

    @Override
    public void onResume() {
        super.onResume();
        if (lazyUpdate) {
            jsonLogListAdapter.notifyDataSetChanged();
            logScrollEnd();
            lazyUpdate = false;
        }
    }

    private void log(String log) {
        requireActivity().runOnUiThread(() -> {
            HashMap<String, String> item = new HashMap<>();
            item.put("log", log);
            this.log.add(item);
            if (context.currentTabIndex == 1) lazyUpdate = true;
            else {
                jsonLogListAdapter.notifyDataSetChanged();
                logScrollEnd();
            }
        });
    }

    private void log(String log, String... errors) {
        requireActivity().runOnUiThread(() -> {
            HashMap<String, String> logItem = new HashMap<>();
            logItem.put("log", log);

            StringBuilder sb = new StringBuilder();
            for (String error : errors) {
                sb.append(error);
            }

            logItem.put("errors", sb.toString());
            this.log.add(logItem);
            for (int i = 0; i < errors.length; i++) {
                String error = errors[i];
                HashMap<String, String> errorItem = new HashMap<>();
                errorItem.put("line_number", Utils.formatLineNumber(i));
                errorItem.put("line_data", error);
                this.log.add(errorItem);
            }
            if (context.currentTabIndex == 1) lazyUpdate = true;
            else {
                jsonLogListAdapter.notifyDataSetChanged();
                logScrollEnd();
            }
        });
    }

    private void log(String log, JSONObject response) {
        requireActivity().runOnUiThread(() -> {
            HashMap<String, String> item = new HashMap<>();
            item.put("log", log);
            item.put("response", response.toString());
            this.log.add(item);
            this.log.addAll(Utils.prettifyJson(requireContext(), response, 4));
            if (context.currentTabIndex == 1) lazyUpdate = true;
            else {
                jsonLogListAdapter.notifyDataSetChanged();
                logScrollEnd();
            }
        });
    }

    private void logScrollEnd() {
        jsonLogList.post(() -> {
            int lastItemIndex = jsonLogListAdapter.getCount() - 1;
            jsonLogList.smoothScrollToPosition(lastItemIndex);
            jsonLogList.setSelection(lastItemIndex);
            jsonLogList.smoothScrollToPosition(lastItemIndex);
        });
    }

    private void showProgress() {
        requireActivity().runOnUiThread(() -> {
            setElapsedTime();
            circularProgress.setVisibility(View.VISIBLE);
            context.lastCircularProgressVisibility = circularProgress.getVisibility();
            elapsedTime.setVisibility(View.GONE);
            context.lastElapsedTimeVisibility = elapsedTime.getVisibility();
            request.setEnabled(false);
            request.setText(R.string.progress);
        });
    }

    private void hideProgress() {
        requireActivity().runOnUiThread(() -> {
            circularProgress.setVisibility(View.GONE);
            context.lastCircularProgressVisibility = circularProgress.getVisibility();
            if (context.currentTabIndex == 0) {
                elapsedTime.setVisibility(View.VISIBLE);
                context.lastElapsedTimeVisibility = elapsedTime.getVisibility();
                elapsedTime.setText(getElapsedTime() + "ms");
            }

            if (elapsedTimeRunnable != null) handler.removeCallbacks(elapsedTimeRunnable);

            elapsedTimeRunnable = () -> {
                elapsedTime.setVisibility(View.GONE);
                context.lastElapsedTimeVisibility = elapsedTime.getVisibility();
            };
            handler.postDelayed(elapsedTimeRunnable, delayMillis);

            request.setEnabled(true);
            request.setText(R.string.request);
        });
    }

    private void setElapsedTime() {
        startTime = System.currentTimeMillis();
    }

    private long getElapsedTime() {
        endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private void syncAPIResponse() {
        // 동기식 API 요청
        showProgress();

        APIClient client = new APIClient();
        APIRequest apiRequest;
        try {
            apiRequest = loadData();
        } catch (Exception e) {
            log("동기식 API 요청 실패", e.getClass().getName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
            hideProgress();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject response = client.makeAPIRequest(apiRequest, RequestType.SYNC);
                log("동기식 API 요청 성공", response);
                hideProgress();
            } catch (Exception e) { // IOException | JSONException e
                log("동기식 API 요청 실패", e.getClass().getName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
                hideProgress();
            }
        }).start();
    }

    private void asyncAPIResponse() {
        // 비동기식 API 요청
        showProgress();

        APIClient client = new APIClient();
        APIRequest apiRequest;
        try {
            apiRequest = loadData();
        } catch (Exception e) {
            log("비동기식 API 요청 실패", e.getClass().getName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
            hideProgress();
            return;
        }

        apiRequest.setOnAPIResponseListener(new OnAPIResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                log("비동기식 API 요청 성공", response);
                hideProgress();
            }

            @Override
            public void onFailure(Exception e) {
                log("비동기식 API 요청 실패", e.getClass().getName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
                hideProgress();
            }
        });

        try {
            client.makeAPIRequest(apiRequest, RequestType.ASYNC);
        } catch (Exception e) { // IOException | JSONException e
            log("비동기식 API 요청 실패", e.getClass().getName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
            hideProgress();
        }
    }

    private APIRequest loadData() {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);

        // 기본
        String prefURL = sharedPref.getString("url", Config.getDefaultUrl());
        int prefMethod = sharedPref.getInt("method", Config.getDefaultMethod());

        // 헤더 및 파라미터 공통
        Type type = new TypeToken<ArrayList<Pair<String, String>>>() {
        }.getType();

        // 헤더
        Gson headersGson = new Gson();
        String headersJson = sharedPref.getString("headers", Config.getDefaultHeaders());
        ArrayList<Pair<String, String>> headersSettings = headersGson.fromJson(headersJson, type);

        Map<String, String> prefHeaders = new HashMap<>();
        for (Pair<String, String> pair : headersSettings)
            prefHeaders.put(pair.first, pair.second);

        // 파라미터
        Gson paramsGson = new Gson();
        String paramsJson = sharedPref.getString("params", Config.getDefaultParams());
        ArrayList<Pair<String, String>> paramsSettings = paramsGson.fromJson(paramsJson, type);

        Map<String, String> prefParams = new HashMap<>();
        for (Pair<String, String> pair : paramsSettings)
            prefParams.put(pair.first, pair.second);

        // 바디
        String prefBody = sharedPref.getString("body", Config.getDefaultBody());

        APIRequest apiRequest = new APIRequest(prefURL, HttpMethod.values()[prefMethod]);
        apiRequest.setHeaders(prefHeaders);
        apiRequest.setParams(prefParams);

        Gson gson = new Gson();
        JSONObject jsonObject = gson.fromJson(prefBody, JSONObject.class);
        apiRequest.setBody(jsonObject);
        return apiRequest;
    }
}