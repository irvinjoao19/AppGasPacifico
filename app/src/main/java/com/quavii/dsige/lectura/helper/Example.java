package com.quavii.dsige.lectura.helper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.quavii.dsige.lectura.R;
import com.quavii.dsige.lectura.data.apiServices.ApiServices;
import com.quavii.dsige.lectura.data.dao.interfaces.RegistroImplementation;
import com.quavii.dsige.lectura.data.dao.overMethod.RegistroOver;
import com.quavii.dsige.lectura.data.model.Mensaje;
import com.quavii.dsige.lectura.data.model.Registro;
import com.quavii.dsige.lectura.data.model.SuministroReparto;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Example extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private ApiServices sendInterfaces;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Realm realm = Realm.getDefaultInstance();
        final RegistroImplementation registroImp = new RegistroOver(realm);

        builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        @SuppressLint("InflateParams") View view = LayoutInflater.from(Example.this).inflate(R.layout.dialog_alert, null);

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewTitle.setText("Enviando...");
        builder.setView(view);


        final Observable<RealmResults<Registro>> auditorias = registroImp.getAllRegistroRx(1);
        String mensaje = "";
        int suma = 0;

        auditorias.flatMap(new Function<RealmResults<Registro>, ObservableSource<Mensaje>>() {

            @Override
            public ObservableSource<Mensaje> apply(RealmResults<Registro> registros) throws Exception {
                return Observable.fromIterable(registros).flatMap(new Function<Registro, ObservableSource<Mensaje>>() {
                    @Override
                    public ObservableSource<Mensaje> apply(Registro registro) throws Exception {
                        MultipartBody.Builder b = new MultipartBody.Builder();
                        RequestBody requestBody = b.build();

                        return Observable.zip(Observable.just(registro), sendInterfaces.sendRegistroRx(requestBody), new BiFunction<Registro, Mensaje, Mensaje>() {
                            @Override
                            public Mensaje apply(Registro registro, Mensaje mensaje) throws Exception {


                                if (mensaje != null) {
                                    registroImp.closeOneRegistro(registro, 0);
                                    return mensaje;
                                } else {
                                    return null;
                                }


                            }
                        });
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Mensaje>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Mensaje mensaje) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }


    public Observable<RealmResults<SuministroReparto>> getInspeccionByNoEstadoRx(int estado) {
        return Observable.create(emitter -> {
            try (Realm r = Realm.getDefaultInstance()) {
                r.refresh();
                RealmResults<SuministroReparto> result = r.where(SuministroReparto.class).notEqualTo("Estado", estado).findAll().sort("Id", Sort.DESCENDING);
                RealmChangeListener<RealmResults<SuministroReparto>> realmChangeListener = new RealmChangeListener<RealmResults<SuministroReparto>>() {
                    @Override
                    public void onChange(RealmResults<SuministroReparto> repartos) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(repartos);
                        }
                    }
                };
                result.addChangeListener(realmChangeListener);
                emitter.onNext(result);
                emitter.onComplete();

            } catch (Throwable e) {
                emitter.onError(e);
            }

        });
    }

    public void bucle() {
//        Observable.interval(3000L, TimeUnit.MILLISECONDS)
//                .timeInterval()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(longTimed -> Log.i("TAG", longTimed.toString()));
    }

}
