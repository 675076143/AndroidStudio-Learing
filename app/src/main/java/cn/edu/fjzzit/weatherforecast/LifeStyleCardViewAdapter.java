package cn.edu.fjzzit.weatherforecast;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * 生活指数适配器（CardView版本）
 */
public class LifeStyleCardViewAdapter extends RecyclerView.Adapter<LifeStyleCardViewAdapter.ViewHolder> {

    private Context mType;
    private List<LifeStyle> mLifeStylesList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView typeImg;
        TextView typeText;
        TextView brfText;
        TextView txtText;
        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            typeImg = (ImageView) view.findViewById(R.id.image_view_lifestyle_type_img);
            typeText = (TextView) view.findViewById(R.id.text_view_lifestyle_type);
            brfText = (TextView) view.findViewById(R.id.text_view_lifestyle_brf);
            txtText = (TextView) view.findViewById(R.id.text_view_lifestyle_txt);

        }
    }

    public LifeStyleCardViewAdapter(List<LifeStyle> lifeStyleList){
        mLifeStylesList = lifeStyleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(mType == null){
            mType = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mType).inflate(R.layout.layout_cardview_lifestyle,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LifeStyleCardViewAdapter.ViewHolder viewHolder, int position) {
        LifeStyle lifeStyle = mLifeStylesList.get(position);
        viewHolder.typeText.setText(lifeStyle.getType());
        viewHolder.brfText.setText(lifeStyle.getBrf());
        viewHolder.txtText.setText(lifeStyle.getTxt());
        // 使用Glide而不使用传统设置图片的方式（图片过大会造成内存溢出，Glide能帮助压缩）
        Glide.with(mType).load(lifeStyle.getTypeImg()).into(viewHolder.typeImg);
    }

    @Override
    public int getItemCount() {
        return mLifeStylesList.size();
    }
}
