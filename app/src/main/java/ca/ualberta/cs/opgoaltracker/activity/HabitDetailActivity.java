/*
 * Copyright 2017 Yongjia Huang, Dichong Song, Mingwei Li, Donglin Han, Long Ma,CMPUT301F17T25 CMPUT301, University of Alberta, All Rights Reserved.
 * You may use distribut, or modify this code under terms and conditions of the ode of Student Behavior at University of Alberta
 * You may find a copy of the license in this project. Otherwise please contact jajayongjia@gmail.com
 */

package ca.ualberta.cs.opgoaltracker.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.ualberta.cs.opgoaltracker.Controller.ElasticsearchController;
import ca.ualberta.cs.opgoaltracker.R;
import ca.ualberta.cs.opgoaltracker.exception.NoTitleException;
import ca.ualberta.cs.opgoaltracker.exception.StringTooLongException;
import ca.ualberta.cs.opgoaltracker.models.Habit;
import ca.ualberta.cs.opgoaltracker.models.Restriction;

public class HabitDetailActivity extends AppCompatActivity {

    private EditText titleBox;
    private EditText reasonBox;
    private CalendarView calendarView;
    private CheckBox checkBoxSun;
    private CheckBox checkBoxMon;
    private CheckBox checkBoxTue;
    private CheckBox checkBoxWed;
    private CheckBox checkBoxThur;
    private CheckBox checkBoxFri;
    private CheckBox checkBoxSat;

    private ArrayList<Habit> habitList;
    private int position;
    private Habit habit;
    private String title;
    private String reason;
    private String owner;
    private Date date;
    private ArrayList<Boolean> period;
    private Restriction restriction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_detail);

//        habit = (Habit) getIntent().getExtras().getParcelable("Habit");
        habitList = getIntent().getExtras().getParcelableArrayList("HabitList");
        position = getIntent().getIntExtra("position", 0);
        owner = getIntent().getStringExtra("owner");
        restriction = getIntent().getParcelableExtra("restriction");
        habit = habitList.get(position);

        titleBox = (EditText) findViewById(R.id.editTitleDetail);
        reasonBox = (EditText) findViewById(R.id.editReasonDetail);
        calendarView = (CalendarView) findViewById(R.id.calendarViewDetail);
        checkBoxSun = (CheckBox) findViewById(R.id.checkBoxSunDetail);
        checkBoxMon = (CheckBox) findViewById(R.id.checkBoxMonDetail);
        checkBoxTue = (CheckBox) findViewById(R.id.checkBoxTueDetail);
        checkBoxWed = (CheckBox) findViewById(R.id.checkBoxWedDetail);
        checkBoxThur = (CheckBox) findViewById(R.id.checkBoxThurDetail);
        checkBoxFri = (CheckBox) findViewById(R.id.checkBoxFriDetail);
        checkBoxSat = (CheckBox) findViewById(R.id.checkBoxSatDetail);

        // get original Habit attributes
        title = habit.getHabitType();
        reason = habit.getReason();
        date = habit.getDate();

        // set View with original data
        titleBox.setText(title);
        reasonBox.setText(reason);
        calendarView.setDate(date.getTime(), false, true);
        checkBoxSun.setChecked(habit.getPeriod().get(0));
        checkBoxMon.setChecked(habit.getPeriod().get(1));
        checkBoxTue.setChecked(habit.getPeriod().get(2));
        checkBoxWed.setChecked(habit.getPeriod().get(3));
        checkBoxThur.setChecked(habit.getPeriod().get(4));
        checkBoxFri.setChecked(habit.getPeriod().get(5));
        checkBoxSat.setChecked(habit.getPeriod().get(6));

        // pie chart code from: https://www.youtube.com/watch?v=iS7EgKnyDeY
        // setup pie chart
        int[] progress = habit.getProgress();
        String[] sectorNames = {"Finished", "Not Finished", "Bonus"};

        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < progress.length; i++) {
            pieEntries.add(new PieEntry(progress[i], sectorNames[i]));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Habits progress");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieDataSet);

        // get pie chart
        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieChart.invalidate();

        // get selected date from CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                date = new Date(calendar.getTimeInMillis());
            }
        });
    }

    /**
     * Invoke this method if Save button at the bottom of the activity is pressed.
     * Set the selected Habit object with new attributes value, and then pass the ArrayList<Habit> back to HabitFragment.
     * @param view
     */
    public void buttonSave(View view) {
        title = titleBox.getText().toString();
        reason = reasonBox.getText().toString();

        period = new ArrayList<Boolean>();
        period.add(checkBoxSun.isChecked());
        period.add(checkBoxMon.isChecked());
        period.add(checkBoxTue.isChecked());
        period.add(checkBoxWed.isChecked());
        period.add(checkBoxThur.isChecked());
        period.add(checkBoxFri.isChecked());
        period.add(checkBoxSat.isChecked());

        try {
            habit.setHabitType(title, restriction.getTitleSize());
        } catch (StringTooLongException exc) {
            String titleLimit = String.valueOf(restriction.getTitleSize());
            Toast.makeText(getApplicationContext(), "Title No More Than " + titleLimit + " Characters",
                    Toast.LENGTH_SHORT).show();
            return;
        } catch (NoTitleException exc) {
            Toast.makeText(getApplicationContext(), "Please Enter Title",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            habit.setReason(reason, restriction.getReasonSize());
        } catch (StringTooLongException exc) {
            String reasonLimit = String.valueOf(restriction.getReasonSize());
            Toast.makeText(getApplicationContext(), "Reason No More Than " + reasonLimit + " Characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        habit.setDate(date);
        habit.setPeriod(period);
        habit.setOwner(owner);

        // update this habit in Elasticsearch
        ElasticsearchController.AddHabitsTask updateHabitsTask = new ElasticsearchController.AddHabitsTask();
        updateHabitsTask.execute(habit);


        Intent intent = new Intent(this, MenuPage.class);
        intent.putParcelableArrayListExtra("HabitList", habitList);
        setResult(RESULT_OK,intent);
        finish();
    }

    /**
     * Create an action bar button (the trash bin button)
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_habit_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle button activities
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.habit_detail_delete) {
            // delete habit from Elasticsearch
            String query = "{\n" +
                    "	\"query\": {\n" +
                    "		\"term\": {\"_id\":\"" + habit.getId() + "\"}\n" +
                    "	}\n" +
                    "}";
            ElasticsearchController.DeleteHabitsTask deleteHabitsTask = new ElasticsearchController.DeleteHabitsTask();
            deleteHabitsTask.execute(query);

            // delete current Habit
            habitList.remove(position);

            Intent intent = new Intent(this, MenuPage.class);
            intent.putParcelableArrayListExtra("HabitList", habitList);
            setResult(RESULT_OK,intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
