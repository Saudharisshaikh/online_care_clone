package com.app.mhcsn_cp.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.app.mhcsn_cp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public  class DatePickerFragment3 extends DialogFragment
implements DatePickerDialog.OnDateSetListener {

	EditText editText;
	Context context;
	public DatePickerFragment3(EditText editText, Context context) {
		// TODO Auto-generated constructor stub
		this.editText = editText;
		this.context = context;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerTheme, this, year, month, day);
		datePickerDialog.getDatePicker().setMinDate(new Date().getTime());

		return datePickerDialog;
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {

		month = month +1;

		//DATA.isDateSelected = true;
		//DATA.date = year + "-" + month + "-" + day;
		DATA.date =  month + "/" + day + "/" + year;

		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");
			Date d = simpleDateFormat.parse(DATA.date);
			//SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
			//SimpleDateFormat sdf = new SimpleDateFormat(sharedPrefsHelper.get("date_formate",Constants.DATE_FORMATE_DISPLAY));
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
			DATA.date = sdf.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		editText.setText(DATA.date);
		//setTimeByTimePicker(editText);
		initCustomTimePicker(editText);
	}
	
	
	int hour;int minute;
	public void initCustomTimePicker(final EditText editText) {
		CustomTimePickerDialog customTimePickerDialog =
		new CustomTimePickerDialog(context, new OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute1) {
				
	                hour = hourOfDay;
	                minute = minute1;
	                String timeSet = "";
	                if (hour > 12) {
	                hour -= 12;
	                timeSet = "PM";
	                } else if (hour == 0) {
	                hour += 12;
	                timeSet = "AM";
	                } else if (hour == 12)
	                timeSet = "PM";
	                else
	                timeSet = "AM";

	                String min = "";
	                if (minute < 10)
	                min = "0" + minute ;
	                else
	                min = String.valueOf(minute);

	                // Append in a StringBuilder
	                String aTime = "";
	                if (hour < 10) {
	                	 aTime = new StringBuilder().append("0").append(hour).append(':')
	        	                .append(min ).append(" ").append(timeSet).toString();
					} else {
						 aTime = new StringBuilder().append(hour).append(':')
				                .append(min ).append(" ").append(timeSet).toString();
					}
	                
	                editText.append(" "+aTime);
			}
		}, 1 , 0 , false);//Calendar.HOUR_OF_DAY , Calendar.MINUTE
		customTimePickerDialog.setTitle("Select time");
		customTimePickerDialog.show();
	}
	
	/*int hour;int minute;
	public void setTimeByTimePicker(final EditText editText) {
		 Calendar mcurrentTime = Calendar.getInstance();
		 hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
         minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
         mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute1) {
               // editText.setText( selectedHour + ":" + selectedMinute);
            	// TODO Auto-generated method stub
                hour = hourOfDay;
                minute = minute1;
                String timeSet = "";
                if (hour > 12) {
                hour -= 12;
                timeSet = "PM";
                } else if (hour == 0) {
                hour += 12;
                timeSet = "AM";
                } else if (hour == 12)
                timeSet = "PM";
                else
                timeSet = "AM";

                String min = "";
                if (minute < 10)
                min = "0" + minute ;
                else
                min = String.valueOf(minute);

                // Append in a StringBuilder
                String aTime = new StringBuilder().append(hour).append(':')
                .append(min ).append(" ").append(timeSet).toString();
                editText.append(" "+aTime);

            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
	}*/
}