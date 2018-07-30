package com.example.ocrdemo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ocrdemo.R;
import com.example.ocrdemo.ShowResult;
import com.example.ocrdemo.model.Recond;

import org.litepal.crud.DataSupport;

import java.util.List;

import static com.example.ocrdemo.utils.Util.dateFormat;
import static com.example.ocrdemo.utils.Util.showToast;

public class RecondAdapter extends RecyclerView.Adapter<RecondAdapter.ViewHolder>{

    private Context context;
    private List<Recond> reconds;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View v;
        TextView path;
        TextView result;
        TextView time;

        private ViewHolder(View view){
            super(view);
            v = view;
            path = (TextView) view.findViewById(R.id.img_path);
            result = (TextView) view.findViewById(R.id.result);
            time = (TextView) view.findViewById(R.id.time);
        }
    }

    public RecondAdapter(List<Recond> recondList){
        reconds = recondList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null){
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //item点击详情页
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Recond recond = reconds.get(position);
                Intent intent = new Intent(context, ShowResult.class);
                intent.putExtra("recond", recond);//数据传给详情页
                context.startActivity(intent);
            }
        });
        //item长按删除
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("删除记录")
                        .setMessage("确定删除数据吗？删除后不可恢复呦！请谨慎操作！！！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int position = holder.getAdapterPosition();
                                Recond recond = reconds.get(position);
                                //数据库里删除

                                int flag = DataSupport.deleteAll(Recond.class,"flag = ? and path = ?", recond.getFlag(), recond.getPath());
                                if(flag > 0){
                                    showToast(context, "删除成功");
                                }else{
                                    showToast(context, "删除失败");
                                }
                                //列表里删除元素
                                reconds.remove(position);
                                notifyItemRemoved(position);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recond recond = reconds.get(position);
        holder.path.setText("图片路径："+recond.getPath());

//        StringBuilder string = new StringBuilder();
//        for(int i = 0; i < recond.getResult().size(); i++){
//            string.append(recond.getResult().get(i)+"\n");
//        }
        holder.result.setText(recond.getResult());
        holder.time.setText(dateFormat(recond.getCreationTime()));
    }

    @Override
    public int getItemCount() {
        return reconds.size();
    }

}
