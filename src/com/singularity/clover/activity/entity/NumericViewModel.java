package com.singularity.clover.activity.entity;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.singularity.clover.R;
import com.singularity.clover.activity.entity.TaskViewActivityHelper.ViewHolder;
import com.singularity.clover.entity.EntityPool;
import com.singularity.clover.entity.EntityViewModel;
import com.singularity.clover.entity.Persisable;
import com.singularity.clover.entity.objective.NumericObj;
import com.singularity.clover.util.drag.DragController;

/**
 * @author xiaol
 * Numeric��Ӧ��ͼģ��
 */
public class NumericViewModel implements EntityViewModel{

	private final TaskViewActivityHelper taskViewActivityHelper;
	private OnNumericLongClick mLongClick;
	private OnMinusClick mMinusClick;
	private OnPlusClick mPlusClick;

	NumericViewModel(TaskViewActivityHelper taskViewActivityHelper) {
		this.taskViewActivityHelper = taskViewActivityHelper;
		mLongClick = new OnNumericLongClick();
		mMinusClick = new OnMinusClick();
		mPlusClick = new OnPlusClick();
	}

	public final static int MODEL_EDIT = 0;
	public final static int MODEL_DISPLAY = 1;
	
	@Override
	public View initView() {
		View layout = this.taskViewActivityHelper.mLinflater.inflate(
				R.layout.task_view_numeric_layout,null);
		layout.setTag(TaskViewActivityHelper.ITEM_NUMERIC);
		return layout;
	}

	@Override
	public View changeModel(int model, View layout) {
		View remove = layout.findViewById(R.id.task_view_numeric_remove);
		EditText value = (EditText) layout.findViewById(
				R.id.task_view_numeric_value);
		EditText max = (EditText) layout.findViewById(
				R.id.task_view_numeric_max);
		View slash = layout.findViewById(R.id.task_view_numeric_slash);
		EditText unit = (EditText) layout.findViewById(
				R.id.task_view_numeric_unit);
		TextView text = (TextView) layout.findViewById(
				R.id.task_view_numeric_process_text);
		View plus = layout.findViewById(R.id.task_view_numeric_plus);
		View minus = layout.findViewById(R.id.task_view_numeric_minus);
		View process = layout.findViewById(R.id.task_view_numeric_process);
		
		switch (model) {
		case MODEL_EDIT:
			plus.setVisibility(View.GONE);
			minus.setVisibility(View.GONE);
			text.setVisibility(View.INVISIBLE);
			
			value.setVisibility(View.VISIBLE);
			max.setVisibility(View.VISIBLE);
			slash.setVisibility(View.VISIBLE);
			unit.setVisibility(View.VISIBLE);
			remove.setVisibility(View.VISIBLE);
			remove.setVisibility(View.VISIBLE);
			process.setOnLongClickListener(null);
			minus.setOnClickListener(null);
			plus.setOnClickListener(null);
			break;
		case MODEL_DISPLAY:
			value.setVisibility(View.GONE);
			max.setVisibility(View.GONE);
			slash.setVisibility(View.GONE);
			unit.setVisibility(View.GONE);
			remove.setVisibility(View.GONE);
			
			plus.setVisibility(View.VISIBLE);
			minus.setVisibility(View.VISIBLE);
			text.setVisibility(View.VISIBLE);
			String str = value.getText().toString();
			if(str.equals("")){
				text.setText(str+" "+ unit.getText().toString());
			}else{
				text.setText(str+"/"+
					max.getText().toString()+" "+ unit.getText().toString());
			}
			process.setOnLongClickListener(mLongClick);
			minus.setOnClickListener(mMinusClick);
			plus.setOnClickListener(mPlusClick);
			break;
		}
		return layout;
	}

	@Override
	public View entityToView(Persisable e, View layout) {
		NumericObj numeric = (NumericObj) e;
		EditText editValue = (EditText) layout.
			findViewById(R.id.task_view_numeric_value);
		EditText editMax = (EditText) layout.
			findViewById(R.id.task_view_numeric_max);
		EditText editUnit = (EditText) layout.
			findViewById(R.id.task_view_numeric_unit);
		
		int value = numeric.getValue();
		int max = numeric.getMax();
		
		editValue.setText(Long.toString(value));
		editMax.setText(Long.toString(max));
		editUnit.setText(numeric.getUnit());
		
		TextView text = (TextView) layout.findViewById(
				R.id.task_view_numeric_process_text);
		if(max == 0){
			text.setText(value+" "+ numeric.getUnit());
		}else{
			text.setText(value+"/"+max+" "+ numeric.getUnit());
		}
		
		View processBar = layout.findViewById(
				R.id.task_view_numeric_process_background);
		FrameLayout.LayoutParams barIp = (LayoutParams) processBar.getLayoutParams();
		ImageView indicator = (ImageView)layout.findViewById(
					R.id.task_view_numeric_process_indicator);
		FrameLayout.LayoutParams lp = (LayoutParams) indicator.getLayoutParams();
		if(max != 0){
			lp.setMargins((int) (1.0f*value*barIp.width/max), 0, 0, 0);
		}else{
			int step = value%3;
			lp.setMargins((int) (1.0f*step*barIp.width/2), 0, 0, 0);
		}
			
		layout.requestLayout();
		return layout;
	}

	@Override
	public Persisable viewToEntity(View layout, Persisable e) {
		NumericObj numeric = (NumericObj) e;
		EditText value = (EditText) layout.findViewById(R.id.task_view_numeric_value);
		EditText max = (EditText) layout.findViewById(R.id.task_view_numeric_max);
		EditText unit = (EditText) layout.findViewById(R.id.task_view_numeric_unit);
		String maxStr = max.getText().toString();
		String minStr = value.getText().toString();
		String unitStr = unit.getText().toString();
		if( unitStr.equals("")){
			unitStr = taskViewActivityHelper.
				mActivity.getText(R.string.unit_hint).toString();
		}
		if(maxStr.equals("")){
			numeric.set(0,unitStr);
		}else{
			numeric.set(Integer.parseInt(max.getText().toString()),unitStr);
		}
		
		if(minStr.equals("")){
			numeric.setValue(0);
		}else{
			numeric.setValue(Integer.parseInt(minStr));
		}
		
		numeric.store();
		return numeric;
	}
	
	public class OnMinusClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			View view = (View) v.getParent();	
			ViewHolder holder = (ViewHolder) view.getTag();
			NumericObj numeric = (NumericObj) EntityPool.instance().forId(
					holder.id, holder.tag);
			numeric.setValue(numeric.getValue()-1);
			numeric.store();
			
			int value = numeric.getValue();
			int max = numeric.getMax();
			TextView text = (TextView) view.findViewById(
				R.id.task_view_numeric_process_text);
			if(max != 0){
				text.setText(value+"/"+max+" "+ numeric.getUnit());
			}else{
				text.setText(value+" "+ numeric.getUnit());
			}
			
			View processBar = view.findViewById(
				R.id.task_view_numeric_process_background);
			View indicator = view.findViewById(
					R.id.task_view_numeric_process_indicator);
			FrameLayout.LayoutParams lp = (LayoutParams) indicator.getLayoutParams();
			if(max != 0){
				lp.setMargins((int) (1.0f*value*processBar.getWidth()/(max)), 0, 0, 0);
			}else{
				int step = value%3;
				lp.setMargins((int) (1.0f*step*processBar.getWidth()/2), 0, 0, 0);
			}
			
			view.requestLayout();
		}
	}
	
	public class OnPlusClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			View view = (View) v.getParent();	
			ViewHolder holder = (ViewHolder) view.getTag();
			NumericObj numeric = (NumericObj) EntityPool.instance().forId(
					holder.id, holder.tag);
			numeric.setValue(numeric.getValue()+1);	
			numeric.store();
			
			int value = numeric.getValue();
			int max = numeric.getMax();
			TextView text = (TextView) view.findViewById(
				R.id.task_view_numeric_process_text);
			if(max == 0){
				text.setText(value+" "+ numeric.getUnit());
			}else{
				text.setText(value+"/"+
							max+" "+ numeric.getUnit());
			}
			
			View processBar = view.findViewById(
				R.id.task_view_numeric_process_background);
			View indicator = view.findViewById(
					R.id.task_view_numeric_process_indicator);
			FrameLayout.LayoutParams lp = (LayoutParams) indicator.getLayoutParams();
			if(max != 0){
				lp.setMargins((int) (1.0f*value*processBar.getWidth()/max), 0, 0, 0);
			}else{
				int step = value%3;
				lp.setMargins((int) (1.0f*step*processBar.getWidth()/2), 0, 0, 0);
			}
			
			view.requestLayout();
		}
		
	}
	
	public class OnNumericLongClick implements OnLongClickListener{

		@Override
		public boolean onLongClick(View v) {
			if (!v.isInTouchMode()) 
				return false;	
			View view = (View) v.getParent();
			Object dragInfo = view;
			taskViewActivityHelper.mActivity.mDragController.startDrag (
					view, taskViewActivityHelper.mActivity.mDragLayer, 
				dragInfo, DragController.DRAG_ACTION_MOVE);
			return true;
		}	
	}

	@Override
	public void updateView(View v) {}
}