package com.example.spacewar;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager
{
    private static HighScoreManager mInstance = null;
    private List<PlayerCard> top10Players;
    private Context mContext;

    SharedPreferences firstPlaceScore;
    SharedPreferences secondPlaceScore;
    SharedPreferences thirdPlaceScore;
    SharedPreferences fourPlaceScore;
    SharedPreferences fivePlaceScore;
    SharedPreferences sixPlaceScore;
    SharedPreferences sevenPlaceScore;
    SharedPreferences eightPlaceScore;
    SharedPreferences ninePlaceScore;
    SharedPreferences tenPlaceScore;

    SharedPreferences firstPlaceName;
    SharedPreferences secondPlaceName;
    SharedPreferences thirdPlaceName;
    SharedPreferences fourPlaceName;
    SharedPreferences fivePlaceName;
    SharedPreferences sixPlaceName;
    SharedPreferences sevenPlaceName;
    SharedPreferences eightPlaceName;
    SharedPreferences ninePlaceName;
    SharedPreferences tenPlaceName;


    public static HighScoreManager getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new HighScoreManager(context);
        }

        return mInstance;
    }

    private HighScoreManager(Context context)
    {
        mContext = context;
        initializeSharedPreference();
    }

    public List<PlayerCard> getTop10PlayersList(Context context)
    {
        mContext=context;
        initializeSharedPreference();

        return top10Players;
    }

    private void initializeSharedPreference()
    {
        firstPlaceScore = mContext.getSharedPreferences("FIRST_PLACE_S", Context.MODE_PRIVATE);
        secondPlaceScore = mContext.getSharedPreferences("SEC_PLACE_S", Context.MODE_PRIVATE);
        thirdPlaceScore = mContext.getSharedPreferences("THIRD_PLACE_S", Context.MODE_PRIVATE);
        fourPlaceScore = mContext.getSharedPreferences("FOUR_PLACE_S", Context.MODE_PRIVATE);
        fivePlaceScore = mContext.getSharedPreferences("FIVE_PLACE_S", Context.MODE_PRIVATE);
        sixPlaceScore = mContext.getSharedPreferences("SIX_PLACE_S", Context.MODE_PRIVATE);
        sevenPlaceScore = mContext.getSharedPreferences("SEVEN_PLACE_S", Context.MODE_PRIVATE);
        eightPlaceScore = mContext.getSharedPreferences("EIGHT_PLACE_S", Context.MODE_PRIVATE);
        ninePlaceScore = mContext.getSharedPreferences("NINE_PLACE_S", Context.MODE_PRIVATE);
        tenPlaceScore = mContext.getSharedPreferences("TEN_PLACE_S", Context.MODE_PRIVATE);

        firstPlaceName = mContext.getSharedPreferences("FIRST_PLACE_N", Context.MODE_PRIVATE);
        secondPlaceName = mContext.getSharedPreferences("SEC_PLACE_N", Context.MODE_PRIVATE);
        thirdPlaceName = mContext.getSharedPreferences("THIRD_PLACE_N", Context.MODE_PRIVATE);
        fourPlaceName = mContext.getSharedPreferences("FOUR_PLACE_N", Context.MODE_PRIVATE);
        fivePlaceName = mContext.getSharedPreferences("FIVE_PLACE_N", Context.MODE_PRIVATE);
        sixPlaceName = mContext.getSharedPreferences("SIX_PLACE_N", Context.MODE_PRIVATE);
        sevenPlaceName = mContext.getSharedPreferences("SEVEN_PLACE_N", Context.MODE_PRIVATE);
        eightPlaceName = mContext.getSharedPreferences("EIGHT_PLACE_N", Context.MODE_PRIVATE);
        ninePlaceName = mContext.getSharedPreferences("NINE_PLACE_N", Context.MODE_PRIVATE);
        tenPlaceName = mContext.getSharedPreferences("TEN_PLACE_N", Context.MODE_PRIVATE);

        initializePlayerList();
    }

    private void initializePlayerList() {
        if(top10Players ==null)
            top10Players = new ArrayList<>();

        top10Players.clear();

        top10Players.add(new PlayerCard(firstPlaceScore.getInt("FIRST_PLACE_S",0), firstPlaceName.getString("FIRST_PLACE_N"," ")));
        top10Players.add(new PlayerCard(secondPlaceScore.getInt("SEC_PLACE_S",0), secondPlaceName.getString("SEC_PLACE_N"," ")));
        top10Players.add(new PlayerCard(thirdPlaceScore.getInt("THIRD_PLACE_S",0), thirdPlaceName.getString("THIRD_PLACE_N"," ")));
        top10Players.add(new PlayerCard(fourPlaceScore.getInt("FOUR_PLACE_S",0), fourPlaceName.getString("FOUR_PLACE_N"," ")));
        top10Players.add(new PlayerCard(fivePlaceScore.getInt("FIVE_PLACE_S",0), fivePlaceName.getString("FIVE_PLACE_N"," ")));
        top10Players.add(new PlayerCard(sixPlaceScore.getInt("SIX_PLACE_S",0), sixPlaceName.getString("SIX_PLACE_N"," ")));
        top10Players.add(new PlayerCard(sevenPlaceScore.getInt("SEVEN_PLACE_S",0), sevenPlaceName.getString("SEVEN_PLACE_N"," ")));
        top10Players.add(new PlayerCard(eightPlaceScore.getInt("EIGHT_PLACE_S",0), eightPlaceName.getString("EIGHT_PLACE_N"," ")));
        top10Players.add(new PlayerCard(ninePlaceScore.getInt("NINE_PLACE_S",0), ninePlaceName.getString("NINE_PLACE_N"," ")));
        top10Players.add(new PlayerCard(tenPlaceScore.getInt("TEN_PLACE_S",0), tenPlaceName.getString("TEN_PLACE_N"," ")));
    }

    public void CheckAndInitializeIfNeedToAddToList(int score, String name)
    {
        if (top10Players.get(9).get_Score() < score) {
            top10Players.remove(9);
            top10Players.add(new PlayerCard(score, name));
            Collections.sort(top10Players, new Comparator<PlayerCard>() {
                @Override
                public int compare(PlayerCard o1, PlayerCard o2) {
                    return Integer.compare(o2.get_Score(), o1.get_Score());
                }
            });
            addNewPreferenceToSharedPreference();
        }
    }

    private void addNewPreferenceToSharedPreference() {
        SharedPreferences.Editor editorFirstPlaceScore = firstPlaceScore.edit();
        SharedPreferences.Editor editorSecondPlaceScore = secondPlaceScore.edit();
        SharedPreferences.Editor editorThirdPlaceScore = thirdPlaceScore.edit();
        SharedPreferences.Editor editorFourPlaceScore = fourPlaceScore.edit();
        SharedPreferences.Editor editorFivePlaceScore = fivePlaceScore.edit();
        SharedPreferences.Editor editorSixPlaceScore = sixPlaceScore.edit();
        SharedPreferences.Editor editorSevenPlaceScore = sevenPlaceScore.edit();
        SharedPreferences.Editor editorEightPlaceScore = eightPlaceScore.edit();
        SharedPreferences.Editor editorNinePlaceScore = ninePlaceScore.edit();
        SharedPreferences.Editor editorTenPlaceScore = tenPlaceScore.edit();

        editorFirstPlaceScore.putInt("FIRST_PLACE_S", top10Players.get(0).get_Score());
        editorFirstPlaceScore.commit();
        editorSecondPlaceScore.putInt("SEC_PLACE_S", top10Players.get(1).get_Score());
        editorSecondPlaceScore.commit();
        editorThirdPlaceScore.putInt("THIRD_PLACE_S", top10Players.get(2).get_Score());
        editorThirdPlaceScore.commit();
        editorFourPlaceScore.putInt("FOUR_PLACE_S",top10Players.get(3).get_Score());
        editorFourPlaceScore.commit();
        editorFivePlaceScore.putInt("FIVE_PLACE_S", top10Players.get(4).get_Score());
        editorFivePlaceScore.commit();
        editorSixPlaceScore.putInt("SIX_PLACE_S", top10Players.get(5).get_Score());
        editorSixPlaceScore.commit();
        editorSevenPlaceScore.putInt("SEVEN_PLACE_S",top10Players.get(6).get_Score());
        editorSevenPlaceScore.commit();
        editorEightPlaceScore.putInt("EIGHT_PLACE_S", top10Players.get(7).get_Score());
        editorEightPlaceScore.commit();
        editorNinePlaceScore.putInt("NINE_PLACE_S", top10Players.get(8).get_Score());
        editorNinePlaceScore.commit();
        editorTenPlaceScore.putInt("TEN_PLACE_S", top10Players.get(9).get_Score());
        editorTenPlaceScore.commit();

        SharedPreferences.Editor editorFirstPlaceName = firstPlaceName.edit();
        SharedPreferences.Editor editorSecondPlaceName = secondPlaceName.edit();
        SharedPreferences.Editor editorThirdPlaceName = thirdPlaceName.edit();
        SharedPreferences.Editor editorFourPlaceName = fourPlaceName.edit();
        SharedPreferences.Editor editorFivePlaceName = fivePlaceName.edit();
        SharedPreferences.Editor editorSixPlaceName = sixPlaceName.edit();
        SharedPreferences.Editor editorSevenPlaceName = sevenPlaceName.edit();
        SharedPreferences.Editor editorEightPlaceName = eightPlaceName.edit();
        SharedPreferences.Editor editorNinePlaceName = ninePlaceName.edit();
        SharedPreferences.Editor editorTenPlaceName = tenPlaceName.edit();

        editorFirstPlaceName.putString("FIRST_PLACE_N", top10Players.get(0).get_Name());
        editorFirstPlaceName.commit();
        editorSecondPlaceName.putString("SEC_PLACE_N", top10Players.get(1).get_Name());
        editorSecondPlaceName.commit();
        editorThirdPlaceName.putString("THIRD_PLACE_N", top10Players.get(2).get_Name());
        editorThirdPlaceName.commit();
        editorFourPlaceName.putString("FOUR_PLACE_N",top10Players.get(3).get_Name());
        editorFourPlaceName.commit();
        editorFivePlaceName.putString("FIVE_PLACE_N", top10Players.get(4).get_Name());
        editorFivePlaceName.commit();
        editorSixPlaceName.putString("SIX_PLACE_N", top10Players.get(5).get_Name());
        editorSixPlaceName.commit();
        editorSevenPlaceName.putString("SEVEN_PLACE_N",top10Players.get(6).get_Name());
        editorSevenPlaceName.commit();
        editorEightPlaceName.putString("EIGHT_PLACE_N", top10Players.get(7).get_Name());
        editorEightPlaceName.commit();
        editorNinePlaceName.putString("NINE_PLACE_N", top10Players.get(8).get_Name());
        editorNinePlaceName.commit();
        editorTenPlaceName.putString("TEN_PLACE_N", top10Players.get(9).get_Name());
        editorTenPlaceName.commit();
    }
}
