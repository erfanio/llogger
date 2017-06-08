package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.DriveRecyclerViewAdapter;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class HomeFragment extends Fragment {

    private DriveDao mDriveDao;
    private Query<Drive> mQuery;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.new_drive_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewDriveActivity.class);
                startActivity(intent);

//                Drive drive = new Drive();
//                drive.car = "Mazda";
//                drive.supervisor = "Mom";
//                drive.time = Calendar.getInstance().getTime();
//                drive.location = "Clayton";
//                List<String> paths = new ArrayList<>();
//                paths.add("|xoeFitrtZOE?BGDEJEL?PERCPCRELBLDHHDH?NGNIRKPKPKNININGNGPKNINGJIFCDC@A@?@??????A?????A????@??A??????????");
//                paths.add("dcpeFqqrtZNVPVPVRVRXRZRXPZTZTZTZV\\VZX\\V\\V\\T\\VZT\\T^TXV^R\\R\\R^N`@P^Nb@N`@Nd@Nb@Pb@Nd@Pb@Lf@Pd@Nb@Ld@Nb@Nb@L`@Nb@Lb@Lb@Ld@Jd@Ld@Jb@Jd@Lb@Jd@Jd@Jd@Ld@Jf@Jd@Lb@Jd@Jb@Hb@Ld@Jd@Jd@Lf@Lf@Lf@Jh@Nh@Jf@Nd@Pd@Nb@Pb@P^Rb@Pb@Tb@Pd@Pb@R`@R`@R`@R`@Rb@P`@Rb@R`@N`@P\\L^PZNZLXPVJPJNJNFBNAHGFOFSFSHg@Hc@Hc@B]@i@Bg@@k@@g@Bu@Do@Fs@@m@Bs@Bq@?s@Dq@?m@As@@s@@s@B{@Aw@?u@?y@?y@A{@C{@?}@C}@A_AC}@C}@C}@C}@C{@E_AA}@Ey@A}@C}@?aA?{@@}@F}@D_AF}@H}@Ny@Pw@Ty@Nw@Vw@Xu@Zq@^o@^m@d@k@d@i@d@g@h@c@l@_@j@]l@[n@Yl@Qn@Wr@Il@Mt@Ml@Mn@Mp@Cn@Gn@Oj@Yt@Qj@Wl@Yl@Wj@Yl@[h@]h@a@h@c@f@c@f@c@f@g@b@g@d@k@b@i@b@k@b@i@d@q@b@g@b@i@b@k@d@i@b@i@b@k@b@i@b@g@`@i@`@e@`@g@`@e@`@g@`@e@`@g@`@g@b@g@\\k@`@i@^m@`@k@^k@^m@`@m@^k@\\o@\\q@Zq@\\q@Xs@Vu@Ts@Tu@Rw@Pu@Pu@Nu@Nw@Lw@Jy@Jw@Hw@Hy@Hy@Fy@F}@Hy@D}@F{@Hy@J{@J{@Ly@Ly@Jw@Ny@Nw@Rs@Pu@Ru@Ru@Ru@Ps@Ru@Py@Rw@Rw@Ry@Ny@P{@Ny@L{@J}@H}@J_AD_AJeAHkAEw@?cA?aAC}@E_AE}@I_AIy@G_AEaAG_AA}@E}@A{@Cy@?{@E}@@{@@}@?aAB}@B}@Dy@B_AF{@B{@B}@D_ADw@H{@F{@D}@Fw@F{@Hy@F_AHy@J}@F_AD{@Fy@D{@D}@F{@F}@@_AD{@B_ABw@@}@By@Fy@F_AAy@D{@?cABaA?{@B}@D}@B_AH{@DaALw@Ly@Jy@Pw@Pu@Hq@Lq@PaATw@Nw@Py@Lw@Ru@L}@Jy@Jy@L{@N{@L{@FaAH{@Jy@H}@J{@F}@H{@Ny@D}@D}@J{@J{@H{@D}@F{@F}@B}@D{@D{@D{@H{@B}@By@B}@B{@B_AF{@D{@B}@D{@F_AF{@H{@F{@F{@H{@H{@J{@J{@Dy@Lw@J{@Ly@J{@H{@P{@Ly@L}@H{@F{@Ly@Jw@Jw@Hy@Jw@H{@Lw@Ly@L{@J}@Ju@Hu@P_AL}@N_AHy@Fy@L}@N}@L}@Nw@Ly@Fo@Ly@Du@Fq@Dq@Dq@Fw@Do@@o@@s@Bu@@m@@m@@k@Am@?i@Ck@?o@Eq@Ei@Cs@Eo@Eu@?q@Qc@Ac@K]Gg@Ec@?[BUA[@WDSHQBOBI@A@C?@@@@AABA?@???????????VULKPMRORQTE\\AZAb@@^Df@Bd@@d@Bd@?b@Fb@Fd@?b@Dd@?`@Hf@?f@@Z@d@Bj@BZFZHb@Fj@Fd@Hf@Bb@Bf@Bl@Bt@?d@Dl@?V?j@Ch@Bh@L`@@`@B`@D\\D`@BTJ^Bp@Cf@B^Ff@?\\D\\BZBd@Bb@Bb@F`@Db@Df@D`@@b@Db@Fd@Bd@B\\J^Df@DZH`@Db@B`@DZH`@Bb@Bb@@T@TDVFTAV?J?GFHBDFVCFJHHXE\\ANEPIBOJUBY?[B_@@e@Bi@?i@@i@?m@?i@?k@Ak@Cm@Ci@Go@Ko@Em@Gm@Ee@Cm@Go@Ag@Ek@Gk@Em@Ck@Eg@Ck@Ek@Ei@Ei@?]Ci@@a@Eu@Ee@Ei@Cq@Im@Am@Km@Iq@Gu@Io@Im@Gq@Co@Es@Gm@Gm@Go@Gm@Gm@Em@Gm@Eo@Em@Gk@Em@Ei@Ck@Ei@Ci@Ek@Em@Gm@Gm@Gm@Eo@Ek@Gk@Ei@Ei@Ee@Ec@Ea@E_@EYCUCOAKAIAA");
//                paths.add("~|xeFeoeuZIy@Ci@Go@Eq@Ek@Ck@Is@Ek@Ei@Em@Ai@Ag@?m@?k@Bm@@k@Bk@Dk@Bm@Bq@Fm@Bk@Bm@Bq@Do@Dm@Bm@@i@Dk@Bg@Hk@Di@Dk@?s@Fk@De@Dm@Jg@@o@Dk@@m@He@Fi@?g@@k@Bm@Bk@@m@Bi@Bi@Bk@Fe@Bg@@k@@c@Bi@Do@Bk@Dk@@m@Bq@Bm@Fk@Bq@Bu@Bu@Bq@Cm@@m@Dq@Do@Bk@Dk@Bo@Dm@Dm@Bk@Di@Bi@Bi@Di@Bi@Bi@Bi@Bg@Di@@g@Cg@?e@Cc@Cc@G[E_@E[EUEOESK@M@OFQFUFYF[D_@D]D]Bk@Tg@Je@He@La@Pa@La@Je@Lc@H_@Fa@Fa@Fa@@a@?_@Ie@Mc@Qa@W_@Wc@]c@[Oc@Yi@Y_@Oq@Ks@Uu@Oq@So@Qq@So@Uq@[k@Qi@Oq@Qm@Mm@Km@Kq@Mm@Kq@Gc@Ik@Mm@KcABe@Ci@Ao@@k@?o@Aq@?s@?q@Bm@Bo@@m@Bm@@o@Bi@Bk@?m@Fk@De@@a@B_@FY?YB[BO@W@QAI@KAIAG?FBCABAA???E?K@S@Q@YB]@Y?[@a@?i@@i@Bk@?m@@m@?k@@m@@k@?k@?m@?e@?k@Ag@?k@Co@?o@Am@Bm@?q@Bo@Aq@@o@Bo@@k@@m@Bm@@k@Hk@Bk@Bm@Dk@Bi@Di@Bc@Be@Ba@B_@@_@@YBYFS@M?MBK?G@G??A?A@?A??@AA??A?@????A@?@A?????????????@??????????????????????????????????A???????????????????????????????????????????????????????a@H_@@a@?_@Bg@?i@Bm@Dk@Bm@Fm@Do@Do@Fo@Dk@Ho@Hk@Fk@Bk@Bo@Am@Am@Am@Ik@Km@Mi@Og@Qk@Wa@Wa@Ya@U_@[c@Y_@Y_@Wc@Sc@Wg@Ue@Sg@Mi@Og@Ii@Mg@Ke@Ee@Gc@E[G[EYEWEWE_@E]G]Gg@Ic@Ig@Kk@Gi@Ik@Kk@Ek@Gg@Kk@Ii@Ik@Ki@Ii@Ki@Ki@Mi@Me@Mg@Sg@Se@Uc@Y_@[_@Y]_@[[]]]_@]]Y]][Y[W[Y]WU_@[[[[][Y[]Y[Y]Y]Y]]e@W_@YY]_@]_@Y_@[W_@[[g@W][[W[Y[W[UWWSUWSUSUSQKGSIKQEK?EHKLOLQTQPOPOPQRKPMJGHIBEGEEGKIKGIKQKSKQKMGKGI?IAIBK@EBEBE@A@A@AA????A@@??@?A???????");
//                drive.path = paths;
//                drive.distance = 12350l;
//                drive.dayDuration = 1161l;
//                drive.light = Drive.Light.NIGHT;
//                drive.traffic = Drive.Traffic.LIGHT;
//                drive.weather = Drive.Weather.DRY;
//                // get the drive DAO
//                DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
//                DriveDao driveDao = daoSession.getDriveDao();
//                driveDao.insert(drive);
            }
        });

        CardView loadMore = (CardView) view.findViewById(R.id.main_load_more);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RootActivity) getActivity()).switch_fragment(RootActivity.Fragments.HISTORY);
            }
        });

        // get the drive DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        mDriveDao = daoSession.getDriveDao();

        mQuery = mDriveDao.queryBuilder().orderDesc(DriveDao.Properties.Time).build();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_history_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DriveRecyclerViewAdapter(getShortDriveList());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        return view;
    }

    private List<Drive> getShortDriveList() {
        List<Drive> allDrives = mQuery.list();

        if (allDrives.size() >= 4) {
            return allDrives.subList(0, 4);
        } else {
            return allDrives.subList(0, allDrives.size());
        }
    }

    private void updateDrives() {
        ((DriveRecyclerViewAdapter) mAdapter).updateList(getShortDriveList());
    }


    @Override
    public void onResume() {
        super.onResume();
        updateDrives();
    }
}
