package com.lifeofmle.drinksy.app.common;

import com.lifeofmle.drinksy.app.utilities.DateUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by mle on 2014-04-27.
 */
public class Session implements Serializable {

    private String id;
    private Date lastUpdated;
    private Date startDate;
    private Date endDate;
    private String sessionLabel;

    private boolean isOverlimit;
    private boolean hadFood;
    private boolean isNew;
    private int maximumCount;
    private int remainingCount;
    private String comment;
    private List<SessionItem> items;    

    private int nextDrinkProgress;
    private long nextDrinkDurationInMillis;
    private String nextDrinkTimeMessage;
    private String nextDrinkTimeUnit;

    private boolean hasAlert;
    private SessionItem alert;
    private boolean passedLimit;

    public Session() {
        setId(getGeneratedId());
        setItems(new ArrayList<SessionItem>());
        setNew(true);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionLabel() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(startDate);

        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        sessionLabel = DateUtility.GetDay(dayIndex) + " " + DateUtility.GetTimeOfDay(hour);

        return sessionLabel;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isOverlimit() {
        return isOverlimit;
    }

    public void setOverlimit(boolean isOverlimit) {
        this.isOverlimit = isOverlimit;
    }

    public boolean isHadFood() {
        return hadFood;
    }

    public void setHadFood(boolean hadFood) {
        this.hadFood = hadFood;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public int getMaximumCount() {
        return maximumCount;
    }

    public void setMaximumCount(int maximumCount) {
        this.maximumCount = maximumCount;
        updateCount();
    }

    public int getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(int remainingCount) {
        this.remainingCount = remainingCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<SessionItem> getItems() {
        return items;
    }

    public void setItems(List<SessionItem> items) {
        this.items = items;
    }

    public String getNextDrinkTimeMessage() {
        return nextDrinkTimeMessage;
    }

    public void setNextDrinkTimeMessage(String nextDrinkTimeMessage) {
        this.nextDrinkTimeMessage = nextDrinkTimeMessage;
    }

    public String getNextDrinkTimeUnit() {
        return nextDrinkTimeUnit;
    }

    public void setNextDrinkTimeUnit(String nextDrinkTimeUnit) {
        this.nextDrinkTimeUnit = nextDrinkTimeUnit;
    }

    public int getNextDrinkProgress() {
        return nextDrinkProgress;
    }

    public void setNextDrinkProgress(int nextDrinkProgress) {
        this.nextDrinkProgress = nextDrinkProgress;
    }

    public long getNextDrinkDurationInMillis() {
        return nextDrinkDurationInMillis;
    }

    public void setNextDrinkDurationInMillis(long nextDrinkDurationInMillis) {
        this.nextDrinkDurationInMillis = nextDrinkDurationInMillis;
    }

    public SessionItem getAlert() {
        return alert;
    }

    public void setAlert(SessionItem alert) {
        this.alert = alert;
    }

    public boolean isHasAlert() {
        return hasAlert;
    }

    public void setHasAlert(boolean hasAlert) {
        this.hasAlert = hasAlert;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Session){
            Session other = (Session)o;
            return this.getId().equals(other.getId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    public void addItem(SessionItem item){
        if (!items.contains(item))
        {
            // Add items to top of list
            items.add(0, item);
        }

        setLastUpdated(new Date());

        updateCount();

        updateNextDrinkTime();

        if (remainingCount < 0){
            if (!passedLimit){
                setHasAlert(true);

                SessionItem alert = new SessionItem();
                alert.setItemType(SessionItemType.Alert);
                alert.setMessage("You have gone pass your limit");

                setAlert(alert);
                passedLimit = true;

            } else {
                setHasAlert(false);
                setAlert(null);
            }
        }
    }

    public void updateCount()
    {
        // Check count of items vs maximum count
        // to calculate remaining count
        int drinkCount = 0;

        for (SessionItem item : items){
            if (item.getItemType().equals(SessionItemType.Drink))
                drinkCount++;
        }

        int remain = maximumCount - drinkCount;

        if (remain < 0 && !isOverlimit)
            setOverlimit(true);

        setRemainingCount(remain);
    }

    public void startSession(){
        setNew(false);
        setStartDate(new Date());
    }

    public void endSession(){
        setEndDate(new Date());
    }

    public String getGeneratedId(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    
    public void updateNextDrinkTime(){

        Date oneHourAgo = new Date(System.currentTimeMillis() - (60 * 60 * 1000));

        // Select number of drinks in the last hour
        // We are using a drink an hour as our rate
        int drinksCount = 0;
        Date earliestDrink = new Date();

        for (SessionItem sessionItem : items) {
            if (sessionItem.getItemType().equals(SessionItemType.Drink) && sessionItem.getCreatedDate().compareTo(oneHourAgo) >= 0){
                if (sessionItem.getCreatedDate().compareTo(earliestDrink) < 0){
                    earliestDrink = sessionItem.getCreatedDate();
                }
                drinksCount++;
            }
        }

        // Reduce time by 20mins if water was drank
        int waterCount = 0;

        for (SessionItem sessionItem : items) {
            if (sessionItem.getItemType().equals(SessionItemType.Water) && sessionItem.getCreatedDate().compareTo(oneHourAgo) >= 0){
                waterCount++;
            }
        }

        long millisecondsToNextDrink = (drinksCount * (30* (60 * 1000))) - (waterCount * (20 * (60 * 1000)));
        Date nextDrinkDate = new Date(earliestDrink.getTime() + millisecondsToNextDrink);

        Calendar earliestDrinkCalendar = GregorianCalendar.getInstance();
        earliestDrinkCalendar.setTime(earliestDrink);

        Calendar nextDrinkCalendar = GregorianCalendar.getInstance();
        nextDrinkCalendar.setTime(nextDrinkDate);

        Calendar currentCalendar = GregorianCalendar.getInstance();
        currentCalendar.setTime(new Date());

        long start = earliestDrinkCalendar.getTimeInMillis();
        long current = currentCalendar.getTimeInMillis();
        long next = nextDrinkCalendar.getTimeInMillis();
        long diff = next - current;
        long diffSeconds = diff / 1000;

        double difference = diff;
        long diffMinutes = (long) Math.ceil(difference / (60d * 1000d));

        if (diff < 0){
            setNextDrinkTimeMessage("GO");
            setNextDrinkTimeUnit("");
            setNextDrinkProgress(100);
            setNextDrinkDurationInMillis(0);

            // If minutesToNextDrink is less than now
            // set showNextDrink is false

        } else {

            int progressPercentage = 0;

            if (diffSeconds <= 60){
                // If less than 60 seconds to next drink
                // set unit to seconds
                setNextDrinkTimeMessage(String.valueOf(diffSeconds));
                setNextDrinkTimeUnit("secs");

                //double value = ((double)diffSeconds / (double) 60);
                //progressPercentage = (int) (value * 100);

            } else {
                if (diffMinutes <= 120){
                    // set unit to minutes
                    setNextDrinkTimeMessage(String.valueOf(diffMinutes));
                    setNextDrinkTimeUnit("mins");
                } else if (diffMinutes > 120){
                    setNextDrinkTimeMessage("2");
                    setNextDrinkTimeUnit("hours");
                }
            }

            long timeLeft = next - current;
            long duration = next - start;
            long progress = current-start;

            setNextDrinkDurationInMillis(timeLeft);

            if (duration > 0){

                double value = ((double)progress / (double) duration);
                progressPercentage = (int) (value * 100);
            }

            setNextDrinkProgress(progressPercentage);
        }

    }
}
