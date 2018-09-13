package calc.com.mycalc.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import calc.com.mycalc.R;
import calc.com.mycalc.utils.AppConfig;

public class CashListAdapter extends RecyclerView.Adapter<CashListAdapter.ViewHolder> {

    private static final int ADD = 0;
    private static final int REMOVE = 1;
    List<HashMap<String, Object>> list;


    public CashListAdapter(List<HashMap<String, Object>> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == ADD) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_add, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_remove, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        h.amount.setText(String.valueOf(list.get(pos).get(AppConfig.AMOUNT)));
        h.desc.setText((String) list.get(pos).get(AppConfig.DESCRIPTION));
        h.date.setText(getdate((Long) list.get(pos).get(AppConfig.DATE)));
    }

    @Override
    public int getItemViewType(int position) {
        if (((Long) list.get(position).get(AppConfig.TYPE) == 0)) {
            return ADD;
        } else {
            return REMOVE;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView del;
        TextView amount, desc, date;

        public ViewHolder(@NonNull View v) {
            super(v);
            del = v.findViewById(R.id.del);
            amount = v.findViewById(R.id.amount);
            desc = v.findViewById(R.id.desc);
            date = v.findViewById(R.id.date);
        }
    }

    private String getdate(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yy, hh:mm a");
        String d = format.format(date);
        return d;
    }
}
