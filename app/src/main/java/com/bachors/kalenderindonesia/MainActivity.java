package com.bachors.kalenderindonesia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bachors.kalenderindonesia.apis.Apis;
import com.bachors.kalenderindonesia.apis.Face;
import com.bachors.kalenderindonesia.databinding.ActivityMainBinding;
import com.bachors.kalenderindonesia.decors.HariDecorator;
import com.bachors.kalenderindonesia.decors.JumatDecorator;
import com.bachors.kalenderindonesia.decors.LiburDecorator;
import com.bachors.kalenderindonesia.decors.MySelectorDecorator;
import com.bachors.kalenderindonesia.decors.SabtuDecorator;
import com.bachors.kalenderindonesia.utils.SharedPrefManager;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
    https://github.com/bachors/Android-Kalender-Indonesia
*/

public class MainActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private ActivityMainBinding binding;

    SharedPrefManager sharedPrefManager;
    Face mApiService;
    Context con;
    AlertDialog loading;

    ArrayList<String> dates = new ArrayList<>();
    ArrayList<CalendarDay> jumat;
    ArrayList<CalendarDay> minggu;
    ArrayList<CalendarDay> sabtu;
    ArrayList<CalendarDay> libur;

    CalendarDay kalender;
    CalendarDay kalenderBaru;
    String sTahun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setCancelable(false);
        builder2.setMessage("Please wait ...");
        loading = builder2.create();

        con = this;
        mApiService = Apis.getClient().create(Face.class);
        sharedPrefManager = new SharedPrefManager(this);

        kalender = CalendarDay.today();
        sTahun = String.valueOf(kalender.getYear());
        if(!sharedPrefManager.getSpTahun().equals(sTahun)) {
            loading.show();
            loadApi();
        }else{
            loadKalender();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Uri uri = Uri.parse("https://github.com/bachors/Android-Kalender-Indonesia");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSelected(
            @NonNull MaterialCalendarView widget,
            @NonNull CalendarDay date,
            boolean selected) {

        GregorianCalendar gCal = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDay());
        Calendar uCal = new UmmalquraCalendar();
        uCal.setTime(gCal.getTime());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("d MMMM, y", Locale.US);
        dateFormat2.setCalendar(uCal);
        String hij = dateFormat2.format(uCal.getTime());
        String ket = terjemah(hij);

        try {
            JSONObject ob = new JSONObject(sharedPrefManager.getSpLibur());
            sTahun = String.valueOf(date.getYear());
            if(ob.has(sTahun)) {
                @SuppressLint("DefaultLocale") String k = date.getYear()+"-"+String.format("%02d", date.getMonth())+"-"+String.format("%02d", date.getDay());
                if(ob.getJSONObject(sTahun).has(k)) {
                    ket += "\n\n" + ob.getJSONObject(sTahun).getString(k);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.teks.setText(ket);

    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

        kalenderBaru = date;
        binding.teks.setText("");
        String tmp = date.getYear() + "" + date.getMonth();
        if (!dates.contains(tmp)) {
            dates.add(tmp);
        }
        widget.removeDecorators();
        showKalender(kalenderBaru);
        MySelectorDecorator ms = new MySelectorDecorator(con);
        widget.addDecorator(ms);

    }

    private void loadApi() {
        mApiService.getSurat(sTahun)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.has(sTahun)) {
                                    JSONObject ob = jsonRESULTS.getJSONObject(sTahun);
                                    JSONObject obb = new JSONObject();
                                    obb.put(sTahun, ob);
                                    sharedPrefManager.saveSPString(SharedPrefManager.SP_TAHUN, sTahun);
                                    sharedPrefManager.saveSPString(SharedPrefManager.SP_LIBUR, obb.toString());
                                    loadKalender();
                                } else {
                                    new AlertDialog.Builder(con)
                                            .setTitle("Error")
                                            .setMessage("Data tidak ada.")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", (dialog, id) -> finish())
                                            .show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                new AlertDialog.Builder(con)
                                        .setTitle("Error")
                                        .setMessage("Tidak ada koneksi internet.")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", (dialog, id) -> finish())
                                        .show();
                            }
                        } else {
                            loading.dismiss();
                            new AlertDialog.Builder(con)
                                    .setTitle("Error")
                                    .setMessage("Tidak ada koneksi internet.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", (dialog, id) -> finish())
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loading.dismiss();
                        new AlertDialog.Builder(con)
                                .setTitle("Error")
                                .setMessage("Tidak ada koneksi internet.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", (dialog, id) -> finish())
                                .show();
                    }
                });
    }

    void loadKalender() {

        Calendar cb = Calendar.getInstance();
        cb.set(kalender.getYear(), 12, 1);
        binding.calendarView.setOnDateChangedListener(this);
        binding.calendarView.setOnMonthChangedListener(this);
        binding.calendarView.setDateSelected(kalender, true);
        binding.calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        binding.calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(kalender.getYear(), 1, 1))
                .setMaximumDate(CalendarDay.from(kalender.getYear(), 12, cb.getActualMaximum(Calendar.DATE)))
                .commit();
        MySelectorDecorator ms = new MySelectorDecorator(con);
        binding.calendarView.addDecorator(ms);

        showKalender(kalender);
        dates.add(kalender.getYear()+""+kalender.getMonth());

        GregorianCalendar gCal = new GregorianCalendar(kalender.getYear(), kalender.getMonth()-1, kalender.getDay());
        Calendar uCal = new UmmalquraCalendar();
        uCal.setTime(gCal.getTime());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("d MMMM, y", Locale.US);
        dateFormat2.setCalendar(uCal);
        String hij = dateFormat2.format(uCal.getTime());
        String ket = terjemah(hij);

        try {
            JSONObject ob = new JSONObject(sharedPrefManager.getSpLibur());
            if(ob.has(sTahun)) {
                @SuppressLint("DefaultLocale") String k = kalender.getYear()+"-"+String.format("%02d", kalender.getMonth())+"-"+String.format("%02d", kalender.getDay());
                if(ob.getJSONObject(sTahun).has(k)) {
                    ket += "\n\n" + ob.getJSONObject(sTahun).getString(k);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.teks.setText(ket);

    }

    void showKalender(final CalendarDay c){
        Calendar calendar = Calendar.getInstance();
        calendar.set(c.getYear(), c.getMonth()-1, 1, 1, 0, 0);
        int jumHari = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        jumat = new ArrayList<>();
        minggu = new ArrayList<>();
        sabtu = new ArrayList<>();
        for(int i = 1; i <= jumHari; i++){
            CalendarDay date = CalendarDay.from(c.getYear(), c.getMonth(), i);
            GregorianCalendar gCal = new GregorianCalendar(date.getYear(), date.getMonth()-1, i);
            Calendar uCal = new UmmalquraCalendar();
            uCal.setTime(gCal.getTime());

            SimpleDateFormat postFormater = new SimpleDateFormat("EEEE", Locale.US);
            String newDateStr = postFormater.format(gCal.getTime());
            if(newDateStr.equals("Sunday")){
                minggu.add(date);
            }
            if(newDateStr.equals("Friday")){
                jumat.add(date);
            }
            if(newDateStr.equals("Saturday")){
                sabtu.add(date);
            }

            // hijri
            SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
            dateFormat.setCalendar(uCal);
            String hijri = dateFormat.format(uCal.getTime()); // Tuesday 8 Rabi' al-Awwal, 1433

            binding.calendarView.addDecorator(new HariDecorator(con, date, hijri));
        }
        binding.calendarView.addDecorator(new LiburDecorator(con, minggu));
        binding.calendarView.addDecorator(new JumatDecorator(con, jumat));
        binding.calendarView.addDecorator(new SabtuDecorator(con, sabtu));
        if(!sTahun.equals(String.valueOf(c.getYear()))) {
            sTahun = String.valueOf(c.getYear());
        }
        try {
            JSONObject ob = new JSONObject(sharedPrefManager.getSpLibur());
            if (ob.has(sTahun)) {
                getLibur();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void getLibur() {
        try {
            libur = new ArrayList<>();
            JSONObject ob = new JSONObject(sharedPrefManager.getSpLibur()).getJSONObject(sTahun);
            Iterator<String> iter = ob.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                String[] k = key.split("-");
                CalendarDay cy = CalendarDay.from(Integer.parseInt(k[0]), Integer.parseInt(k[1]), Integer.parseInt(k[2]));
                libur.add(cy);
            }
            binding.calendarView.addDecorator(new LiburDecorator(con, libur));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String terjemah(String hij) {
        return hij.replaceAll("0001", "Muharram").replaceAll("0002", "Safar").replaceAll("0003", "Rabi’ul Awal").replaceAll("0004", "Rabi’ul Akhir").replaceAll("0005", "Jumadil Awal").replaceAll("0006", "Jumadil Akhir").replaceAll("0007", "Rajab").replaceAll("0008", "Sya’ban").replaceAll("0009", "Ramadhan").replaceAll("0010", "Syawal").replaceAll("0011", "Dzulka’dah").replaceAll("0012", "Dzulhijah");
    }

}