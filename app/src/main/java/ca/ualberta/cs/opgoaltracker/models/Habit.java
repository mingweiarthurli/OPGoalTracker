/*
 * Copyright 2017 Yongjia Huang, Dichong Song, Mingwei Li, Donglin Han, Long Ma,CMPUT301F17T25 CMPUT301, University of Alberta, All Rights Reserved.
 * You may use distribut, or modify this code under terms and conditions of the ode of Student Behavior at University of Alberta
 * You may find a copy of the license in this project. Otherwise please contact jajayongjia@gmail.com
 */

package ca.ualberta.cs.opgoaltracker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ca.ualberta.cs.opgoaltracker.exception.NoTitleException;
import ca.ualberta.cs.opgoaltracker.exception.StringTooLongException;


/**
 * This Habit Object allows User to do the implementation to the Habit <br>
 *     This Habit object is passable by intent<br>
 *
 * @author Dichong Song, Yongjia Huang, Mingwei Li
 * @version 3.0
 * @see Parcelable
 * @since 1.0
 */
public class Habit implements Parcelable, Comparable<Habit> {

    private String id;
    private String owner;
    private String habitType;
    private String reason;
    private Date date;
    private Date createDate;
    private ArrayList<Boolean> period;
    private ArrayList<HabitEvent> eventList;

    /**
     * Basic Habit Constructor, allows user to create a Habit object by defining habit name ( habit type), date, reason ,starttime and intervaltime.<br>
     *
     * @param habitType : String
     * @param reason : String
     * @param date : Date
     * @param period : ArrayList<Boolean>
     * @param titleSize : int
     * @param reasonSize : int
     * @throws StringTooLongException
     * @throws NoTitleException
     */
    public Habit(String habitType, String reason, Date date, ArrayList<Boolean> period, int titleSize, int reasonSize) throws StringTooLongException, NoTitleException {
        if (habitType.length() > titleSize){
            throw new StringTooLongException();
        }
        if(habitType.equals("")){
            throw new NoTitleException();
        }
        if (reason.length() > titleSize){
            throw new StringTooLongException();
        }

        this.id = java.util.UUID.randomUUID().toString();
        this.habitType = habitType;
        this.date = date;
        this.createDate = new Date();
        this.reason = reason;
        this.period = period;
        this.eventList=new ArrayList<HabitEvent>();
    }


    // start of implementing Parcelable
    /**
     * Default Parcel method , implement Parcelable
     * @see Parcelable
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Default Parcel method , implement Parcelable
     * @see Parcelable
     * @param out
     * @param i
     */
    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(id);
        out.writeString(owner);
        out.writeString(habitType);
        out.writeString(reason);
        // Write long value of Date
        out.writeLong(date.getTime());
        out.writeLong(createDate.getTime());
        out.writeList(period);
        out.writeList(eventList);
    }

    /**
     * Default Parcel method , implement Parcelable
     * @see Parcelable
     * @param in
     */
    private void readFromParcel(Parcel in) {
        id = in.readString();
        owner = in.readString();
        habitType = in.readString();
        reason = in.readString();
        // Read Long value and convert to date
        date = new Date(in.readLong());
        createDate = new Date(in.readLong());
        period = in.readArrayList(null);
        eventList=in.readArrayList(HabitEvent.class.getClassLoader());
    }

    /**
     * Default Parcel method , implement Parcelable
     * @see Parcelable
     * @param in
     */
    protected Habit(Parcel in) {
        readFromParcel(in);
    }

    /**
     * Default Parcel method , implement Parcelable
     * @see Parcelable
     */
    public static final Creator<Habit> CREATOR = new Creator<Habit>() {
        @Override
        public Habit createFromParcel(Parcel in) {
            return new Habit(in);
        }

        @Override
        public Habit[] newArray(int size) {
            return new Habit[size];
        }
    };
    // end of implementing Parcelable

    /**
     * Set id for the Habit object
     * @return id : String
     */
    public String getId() {
        return id;
    }

    /**
     * Get id of the Habit object
     * @param id : String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Basic Habit Type getter
     * @return habitType : String
     */
    public String getHabitType() {
        return habitType;
    }

    /**
     * Basic Reason getter
     * @return reason : String
     */
    public String getReason() {
        return reason;
    }

    /**
     * Basic Date getter
     * @return date : Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Basic HabitType setter
     * @param habitType: String
     * @param titleSize: String
     * @throws StringTooLongException
     * @throws NoTitleException
     */
    public void setHabitType(String habitType, int titleSize) throws StringTooLongException,NoTitleException {
        if (habitType.length() > titleSize){
            throw new StringTooLongException();
        }
        if(habitType.equals("")){
            throw new NoTitleException();
        }
        this.habitType = habitType;
    }

    /**
     * Basic Reason Setter
     * @param reason: String
     * @param reasonSize: String
     * @throws StringTooLongException
     */
    public void setReason(String reason, int reasonSize) throws StringTooLongException{
        if (reason.length() > reasonSize){
            throw new StringTooLongException();
        }
        this.reason = reason;
    }

    /**
     * Basic Date setter
     * @param date: Long
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Basic period getter
     * @return period ArrayList<Boolean>
     */
    public ArrayList<Boolean> getPeriod() {return period; }

    /**
     * Basic period setter
     * @param period
     */
    public void setPeriod(ArrayList<Boolean> period) {
        this.period = period;
    }

    /**
     * Basic createDate getter
     * @return createDate : Date
     */
    public Date getCreateDate() {
        return createDate;
    }

    public boolean isTodo() {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(this.date);
        Calendar currentDate = Calendar.getInstance();

        if ((startDate.get(Calendar.YEAR) < currentDate.get(Calendar.YEAR)) ||
                (startDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                        startDate.get(Calendar.DAY_OF_YEAR) <= currentDate.get(Calendar.DAY_OF_YEAR))) { // if habit start date is same or before today
            if (this.period.get(currentDate.get(Calendar.DAY_OF_WEEK) - 1)) { // if today in the week is in the habit period
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int getTotalDays() {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(this.date);
        Calendar currentDate = Calendar.getInstance();

        int totalDays = 0;

        while (startDate.get(Calendar.YEAR) < currentDate.get(Calendar.YEAR) ||
                (startDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                        startDate.get(Calendar.DAY_OF_YEAR) <= currentDate.get(Calendar.DAY_OF_YEAR))) { // if habit start date is same or before today
            if (this.period.get(startDate.get(Calendar.DAY_OF_WEEK) - 1)) { // if this date is in the period
                totalDays++;
            }
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        return totalDays;
    }

    public int[] getProgress() {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(this.date);
        Calendar eventDate = Calendar.getInstance();

        int finished = 0;
        int notFinished;
        int bonus = 0;
        int [] progress = new int[3];

        for (HabitEvent habitEvent : this.eventList) {
            eventDate.setTime(habitEvent.getDate());

            if (startDate.get(Calendar.YEAR) < eventDate.get(Calendar.YEAR) ||
                    (startDate.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR) &&
                            startDate.get(Calendar.DAY_OF_YEAR) <= eventDate.get(Calendar.DAY_OF_YEAR))) { // if habit start date is same or before this event date
                if (this.period.get(eventDate.get(Calendar.DAY_OF_WEEK) - 1)) { // if this event date was scheduled
                    finished++;
                } else {
                    bonus++;
                }
            } else {
                bonus++;
            }
        }

        notFinished = getTotalDays() - finished;
        if (notFinished < 0) {
            notFinished = 0;
        }
        progress[0] = finished;
        progress[1] = notFinished;
        progress[2] = bonus;

        return progress;
    }

    /**
     * Override compareTo to implement the sort method of HabitList
     * @param compareHabit
     * @see Comparable
     * @return
     */
    @Override
    public int compareTo(Habit compareHabit) {
        return getCreateDate().compareTo(compareHabit.getCreateDate());
    }

    public void setOwner(String owner){this.owner=owner;}
    public ArrayList<HabitEvent> getEventList(){
        return eventList;
    }
    public HabitEvent getLatest() {
        if (eventList.size() > 0) {
            return eventList.get(eventList.size() - 1);
        }
        else{
            return null;
        }
    }
    public void newEvent(HabitEvent a){
        eventList.add(a);
    }
    public void removeEvent(HabitEvent a){
        eventList.remove(a);
    }

}
