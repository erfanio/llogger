package io.erfan.llogger.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.erfan.llogger.App;
import io.erfan.llogger.PrefMan;
import io.erfan.llogger.R;
import io.erfan.llogger.Utils;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static int WRITE_REQUEST = 0;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.app_preferences);

        Preference addShort = findPreference("short");
        addShort.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                createShortLog();
                Toast.makeText(getContext(), "Log created!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference addOne = findPreference("one");
        addOne.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                createLongLog();
                Toast.makeText(getContext(), "Log created!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference addTen = findPreference("ten");
        addTen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                for (int i = 0; i < 10; i++) {
                    createLongLog();
                }
                Toast.makeText(getContext(), "Logs created!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference addThirty = findPreference("thirty");
        addThirty.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                for (int i = 0; i < 30; i++) {
                    createLongLog();
                }
                Toast.makeText(getContext(), "Logs created!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference save = findPreference("save");
        save.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!Settings.System.canWrite(getContext())) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_REQUEST);
                } else {
                    save();
                }
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_REQUEST) {
            save();
        }
    }

    private void save() {
        // get the directory (or create it)
        File directory = new File(Environment.getExternalStorageDirectory(), "LearnersLog");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Toast.makeText(getContext(), "Writing to file failed", Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        final File outputFile = new File(directory,
                String.format("logs-%s.csv", dateFormat.format(Calendar.getInstance().getTime())));
        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(getCSV());
            writer.flush();
            writer.close();

            Snackbar.make(getView(),
                    String.format("Logs have been saved to %s", outputFile.getAbsolutePath()),
                    Snackbar.LENGTH_LONG)
                    .setAction("Open", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(".CSV");

                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(FileProvider.getUriForFile(getContext(),
                                    "io.erfan.llogger.provider", outputFile), mime);
                            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }
                    })
                    .show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Writing to file failed", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private String getCSV() {
        PrefMan prefMan = new PrefMan(getContext());
        // get the list of drives that the current user has done
        List<Drive> drives = ((App) getContext().getApplicationContext()).getDaoSession()
                .getDriveDao().queryBuilder().where(DriveDao.Properties.DriverId.eq(prefMan.getUser()))
                .list();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String format = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n";
        String out = "Date,Start Time,Finish Time,Total This Trip,Total Total,Night This Trip,Night Total," +
                "Odometer Start,Odometer Finish,Car Rego,Parking,Traffic Light,Traffic Moderate,Traffic Heavy," +
                "Weather Dry,Weather Wet,Road Local St,Road Main Rd,Road Inner City,Road Freeway,Road Rural Hwy," +
                "Road Rural Other,Road Gravel,Light Day,Light Dawn/Dusk,Night,Supervisor Licence,Supervisor Name\n";

        long total = 0;
        long totalNight = 0;
        for (Drive drive : drives) {
            String date = dateFormat.format(drive.getTime());
            String startTime = timeFormat.format(drive.getTime());

            Calendar finishCal = Calendar.getInstance();
            finishCal.setTime(drive.getTime());
            finishCal.add(Calendar.SECOND, (drive.getDayDuration().intValue() + drive.getNightDuration().intValue()));
            String finishTime = timeFormat.format(finishCal.getTime());

            total += drive.getDayDuration() + drive.getNightDuration();
            String totalDuration = drive.getFormattedDuration();
            String totalTotalDuration = Utils.formatDuration(total);

            totalNight += drive.getNightDuration();
            String nightDuration = Utils.formatDuration(drive.getNightDuration());
            String nightTotalDuration = Utils.formatDuration(totalNight);

            String odometerStart = drive.getOdometer().toString();
            // start + distance in KM
            String odometerEnd = String.valueOf(drive.getOdometer() + (drive.getDistance() / 1000));

            String rego = drive.getCar().getPlateNo();

            String parking = (drive.getParking()) ? "Yes" : "No";
            String trafficLight = (drive.getTraffic() == Drive.Traffic.LIGHT) ? "Yes" : "No";
            String trafficMedium = (drive.getTraffic() == Drive.Traffic.MEDIUM) ? "Yes" : "No";
            String trafficHeavy = (drive.getTraffic() == Drive.Traffic.HEAVY) ? "Yes" : "No";
            String weatherDry = (drive.getWeather() == Drive.Weather.DRY) ? "Yes" : "No";
            String weatherWet = (drive.getWeather() == Drive.Weather.WET) ? "Yes" : "No";
            String roadLocal = (drive.getRoadLocal()) ? "Yes" : "No";
            String roadMain = (drive.getRoadMain()) ? "Yes" : "No";
            String roadCity = (drive.getRoadCity()) ? "Yes" : "No";
            String roadFreeway = (drive.getRoadFreeway()) ? "Yes" : "No";
            String roadRuralHwy = (drive.getRoadRuralHwy()) ? "Yes" : "No";
            String roadRuralOther = (drive.getRoadRuralOther()) ? "Yes" : "No";
            String roadGravel = (drive.getRoadGravel()) ? "Yes" : "No";
            String lightDay = (drive.getLight() == Drive.Light.DAY) ? "Yes" : "No";
            String lightDawnDusk = (drive.getLight() == Drive.Light.DAWN_DUSK) ? "Yes" : "No";
            String lightNight = (drive.getLight() == Drive.Light.NIGHT) ? "Yes" : "No";

            String supervisorLicence = drive.getSupervisor().getLicenceNo();
            String supervisor = drive.getSupervisor().getName();

            out += String.format(format, date, startTime, finishTime, totalDuration, totalTotalDuration,
                    nightDuration, nightTotalDuration, odometerStart, odometerEnd, rego, parking, trafficLight,
                    trafficMedium, trafficHeavy, weatherDry, weatherWet, roadLocal, roadMain, roadCity,
                    roadFreeway, roadRuralHwy, roadRuralOther, roadGravel, lightDay, lightDawnDusk, lightNight,
                    supervisorLicence, supervisor);
        }

        return out;
    }

    private static Long mSupervisorId;
    private static Long mCarId;
    private void createLongLog() {
        PrefMan prefMan = new PrefMan(getContext());
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();

        Drive drive = new Drive();

        drive.setDriverId(prefMan.getUser());
        if (mSupervisorId == null) {
            mSupervisorId = daoSession.getSupervisorDao().queryBuilder().limit(1).list().get(0).getId();
        }
        drive.setSupervisorId(mSupervisorId);
        if (mCarId == null) {
            mCarId = daoSession.getCarDao().queryBuilder().limit(1).list().get(0).getId();
        }
        drive.setCarId(mCarId);
        drive.setTime(Calendar.getInstance().getTime());
        drive.setOdometer(1324);
        drive.setLocation("Bulleen");
        List<String> paths = new ArrayList<>();
        paths.add("|xoeFitrtZOE?BGDEJEL?PERCPCRELBLDHHDH?NGNIRKPKPKNININGNGPKNINGJIFCDC@A@?@??????A?????A????@??A??????????");
        paths.add("dcpeFqqrtZNVPVPVRVRXRZRXPZTZTZTZV\\VZX\\V\\V\\T\\VZT\\T^TXV^R\\R\\R^N`@P^Nb@N`@Nd@Nb@Pb@Nd@Pb@Lf@Pd@Nb@Ld@Nb@Nb@L`@Nb@Lb@Lb@Ld@Jd@Ld@Jb@Jd@Lb@Jd@Jd@Jd@Ld@Jf@Jd@Lb@Jd@Jb@Hb@Ld@Jd@Jd@Lf@Lf@Lf@Jh@Nh@Jf@Nd@Pd@Nb@Pb@P^Rb@Pb@Tb@Pd@Pb@R`@R`@R`@R`@Rb@P`@Rb@R`@N`@P\\L^PZNZLXPVJPJNJNFBNAHGFOFSFSHg@Hc@Hc@B]@i@Bg@@k@@g@Bu@Do@Fs@@m@Bs@Bq@?s@Dq@?m@As@@s@@s@B{@Aw@?u@?y@?y@A{@C{@?}@C}@A_AC}@C}@C}@C}@C{@E_AA}@Ey@A}@C}@?aA?{@@}@F}@D_AF}@H}@Ny@Pw@Ty@Nw@Vw@Xu@Zq@^o@^m@d@k@d@i@d@g@h@c@l@_@j@]l@[n@Yl@Qn@Wr@Il@Mt@Ml@Mn@Mp@Cn@Gn@Oj@Yt@Qj@Wl@Yl@Wj@Yl@[h@]h@a@h@c@f@c@f@c@f@g@b@g@d@k@b@i@b@k@b@i@d@q@b@g@b@i@b@k@d@i@b@i@b@k@b@i@b@g@`@i@`@e@`@g@`@e@`@g@`@e@`@g@`@g@b@g@\\k@`@i@^m@`@k@^k@^m@`@m@^k@\\o@\\q@Zq@\\q@Xs@Vu@Ts@Tu@Rw@Pu@Pu@Nu@Nw@Lw@Jy@Jw@Hw@Hy@Hy@Fy@F}@Hy@D}@F{@Hy@J{@J{@Ly@Ly@Jw@Ny@Nw@Rs@Pu@Ru@Ru@Ru@Ps@Ru@Py@Rw@Rw@Ry@Ny@P{@Ny@L{@J}@H}@J_AD_AJeAHkAEw@?cA?aAC}@E_AE}@I_AIy@G_AEaAG_AA}@E}@A{@Cy@?{@E}@@{@@}@?aAB}@B}@Dy@B_AF{@B{@B}@D_ADw@H{@F{@D}@Fw@F{@Hy@F_AHy@J}@F_AD{@Fy@D{@D}@F{@F}@@_AD{@B_ABw@@}@By@Fy@F_AAy@D{@?cABaA?{@B}@D}@B_AH{@DaALw@Ly@Jy@Pw@Pu@Hq@Lq@PaATw@Nw@Py@Lw@Ru@L}@Jy@Jy@L{@N{@L{@FaAH{@Jy@H}@J{@F}@H{@Ny@D}@D}@J{@J{@H{@D}@F{@F}@B}@D{@D{@D{@H{@B}@By@B}@B{@B_AF{@D{@B}@D{@F_AF{@H{@F{@F{@H{@H{@J{@J{@Dy@Lw@J{@Ly@J{@H{@P{@Ly@L}@H{@F{@Ly@Jw@Jw@Hy@Jw@H{@Lw@Ly@L{@J}@Ju@Hu@P_AL}@N_AHy@Fy@L}@N}@L}@Nw@Ly@Fo@Ly@Du@Fq@Dq@Dq@Fw@Do@@o@@s@Bu@@m@@m@@k@Am@?i@Ck@?o@Eq@Ei@Cs@Eo@Eu@?q@Qc@Ac@K]Gg@Ec@?[BUA[@WDSHQBOBI@A@C?@@@@AABA?@???????????VULKPMRORQTE\\AZAb@@^Df@Bd@@d@Bd@?b@Fb@Fd@?b@Dd@?`@Hf@?f@@Z@d@Bj@BZFZHb@Fj@Fd@Hf@Bb@Bf@Bl@Bt@?d@Dl@?V?j@Ch@Bh@L`@@`@B`@D\\D`@BTJ^Bp@Cf@B^Ff@?\\D\\BZBd@Bb@Bb@F`@Db@Df@D`@@b@Db@Fd@Bd@B\\J^Df@DZH`@Db@B`@DZH`@Bb@Bb@@T@TDVFTAV?J?GFHBDFVCFJHHXE\\ANEPIBOJUBY?[B_@@e@Bi@?i@@i@?m@?i@?k@Ak@Cm@Ci@Go@Ko@Em@Gm@Ee@Cm@Go@Ag@Ek@Gk@Em@Ck@Eg@Ck@Ek@Ei@Ei@?]Ci@@a@Eu@Ee@Ei@Cq@Im@Am@Km@Iq@Gu@Io@Im@Gq@Co@Es@Gm@Gm@Go@Gm@Gm@Em@Gm@Eo@Em@Gk@Em@Ei@Ck@Ei@Ci@Ek@Em@Gm@Gm@Gm@Eo@Ek@Gk@Ei@Ei@Ee@Ec@Ea@E_@EYCUCOAKAIAA");
        paths.add("~|xeFeoeuZIy@Ci@Go@Eq@Ek@Ck@Is@Ek@Ei@Em@Ai@Ag@?m@?k@Bm@@k@Bk@Dk@Bm@Bq@Fm@Bk@Bm@Bq@Do@Dm@Bm@@i@Dk@Bg@Hk@Di@Dk@?s@Fk@De@Dm@Jg@@o@Dk@@m@He@Fi@?g@@k@Bm@Bk@@m@Bi@Bi@Bk@Fe@Bg@@k@@c@Bi@Do@Bk@Dk@@m@Bq@Bm@Fk@Bq@Bu@Bu@Bq@Cm@@m@Dq@Do@Bk@Dk@Bo@Dm@Dm@Bk@Di@Bi@Bi@Di@Bi@Bi@Bi@Bg@Di@@g@Cg@?e@Cc@Cc@G[E_@E[EUEOESK@M@OFQFUFYF[D_@D]D]Bk@Tg@Je@He@La@Pa@La@Je@Lc@H_@Fa@Fa@Fa@@a@?_@Ie@Mc@Qa@W_@Wc@]c@[Oc@Yi@Y_@Oq@Ks@Uu@Oq@So@Qq@So@Uq@[k@Qi@Oq@Qm@Mm@Km@Kq@Mm@Kq@Gc@Ik@Mm@KcABe@Ci@Ao@@k@?o@Aq@?s@?q@Bm@Bo@@m@Bm@@o@Bi@Bk@?m@Fk@De@@a@B_@FY?YB[BO@W@QAI@KAIAG?FBCABAA???E?K@S@Q@YB]@Y?[@a@?i@@i@Bk@?m@@m@?k@@m@@k@?k@?m@?e@?k@Ag@?k@Co@?o@Am@Bm@?q@Bo@Aq@@o@Bo@@k@@m@Bm@@k@Hk@Bk@Bm@Dk@Bi@Di@Bc@Be@Ba@B_@@_@@YBYFS@M?MBK?G@G??A?A@?A??@AA??A?@????A@?@A?????????????@??????????????????????????????????A???????????????????????????????????????????????????????a@H_@@a@?_@Bg@?i@Bm@Dk@Bm@Fm@Do@Do@Fo@Dk@Ho@Hk@Fk@Bk@Bo@Am@Am@Am@Ik@Km@Mi@Og@Qk@Wa@Wa@Ya@U_@[c@Y_@Y_@Wc@Sc@Wg@Ue@Sg@Mi@Og@Ii@Mg@Ke@Ee@Gc@E[G[EYEWEWE_@E]G]Gg@Ic@Ig@Kk@Gi@Ik@Kk@Ek@Gg@Kk@Ii@Ik@Ki@Ii@Ki@Ki@Mi@Me@Mg@Sg@Se@Uc@Y_@[_@Y]_@[[]]]_@]]Y]][Y[W[Y]WU_@[[[[][Y[]Y[Y]Y]Y]]e@W_@YY]_@]_@Y_@[W_@[[g@W][[W[Y[W[UWWSUWSUSUSQKGSIKQEK?EHKLOLQTQPOPOPQRKPMJGHIBEGEEGKIKGIKQKSKQKMGKGI?IAIBK@EBEBE@A@A@AA????A@@??@?A???????");
        drive.setPath(paths);
        drive.setDistance(12350L);
        drive.setDayDuration(1161L);
        drive.setNightDuration(161L);
        drive.setLight(Drive.Light.NIGHT);
        drive.setTraffic(Drive.Traffic.LIGHT);
        drive.setWeather(Drive.Weather.DRY);
        drive.setParking(true);
        drive.setRoadLocal(false);
        drive.setRoadMain(false);
        drive.setRoadCity(false);
        drive.setRoadFreeway(false);
        drive.setRoadRuralHwy(false);
        drive.setRoadRuralOther(false);
        drive.setRoadGravel(false);

        // insert into database
        DriveDao driveDao = daoSession.getDriveDao();
        driveDao.insert(drive);
    }

    public void createShortLog() {PrefMan prefMan = new PrefMan(getContext());
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();

        Drive drive = new Drive();

        drive.setDriverId(prefMan.getUser());
        if (mSupervisorId == null) {
            mSupervisorId = daoSession.getSupervisorDao().queryBuilder().limit(1).list().get(0).getId();
        }
        drive.setSupervisorId(mSupervisorId);
        if (mCarId == null) {
            mCarId = daoSession.getCarDao().queryBuilder().limit(1).list().get(0).getId();
        }
        drive.setCarId(mCarId);
        drive.setTime(Calendar.getInstance().getTime());
        drive.setOdometer(13245);
        drive.setLocation("Ringwood East");
        List<String> paths = new ArrayList<>();
        paths.add("h|ueF}avuZ???A?@CACA??MCMEMASAQASESCQCUESAQCQEQAOAKAKEKAG?CACAA@@?A?????????A???????????A?A?A???C@CACACCE?CAA?A?A?AB?J@L@NBRDVFXHXHXH\\J^H\\J`@L`@H`@J`@Jb@L^J`@L^J`@L`@J`@J`@Lb@Hb@J^L\\H^J^F^J^J\\F\\J^H\\F\\JXFXFVJXFTBTDRDPDJJ^AA@CAA@A?A?A@????A@??@CJ?NBN@R@V@X@X?\\?^CZC`@E`@E`@G`@G`@I`@Ib@Ib@Kf@Kh@Kj@Il@Kl@Ij@Ih@Gj@Ih@Gj@Gl@Gj@Al@Cj@@l@Bj@@n@Dl@Bn@Dn@Dn@Dp@Dp@Dp@Dp@Dn@Fn@Dn@Fl@Dj@Fl@Dj@Bj@Dh@Fh@Fj@Fl@Dh@Fl@Dj@Fj@Dj@Bl@@n@Hl@Dn@Bn@Jj@Dj@Dj@Bh@Bh@Bd@Bd@BZB\\@\\A\\@Z@ZBXDXDTBTDNDJDF@B@@?@?@????A????????????????@???????????????????????????????????????????????????????????????????????????????????@@@?D?DEHGFIFKHOLOPOPQRSTOPOPKLIJKJI@GCGIEMKOMQIMKQMOIMIMCGCI@K@EBCBCBC@C@A");
        drive.setPath(paths);
        drive.setDistance(2500L);
        drive.setDayDuration(297L);
        drive.setNightDuration(0L);
        drive.setLight(Drive.Light.DAY);
        drive.setTraffic(Drive.Traffic.LIGHT);
        drive.setWeather(Drive.Weather.DRY);
        drive.setParking(true);
        drive.setRoadLocal(true);
        drive.setRoadMain(true);
        drive.setRoadCity(false);
        drive.setRoadFreeway(false);
        drive.setRoadRuralHwy(false);
        drive.setRoadRuralOther(false);
        drive.setRoadGravel(false);

        // insert into database
        DriveDao driveDao = daoSession.getDriveDao();
        driveDao.insert(drive);
    }
}