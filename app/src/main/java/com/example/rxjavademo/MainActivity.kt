package com.example.rxjavademo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MainActivity : AppCompatActivity() {

    val TAG1 = "types"
    val TAG2 = "operators"
    val TAG3 = "filter"

    val TAG = "observables"

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Observable.just("Apple", "Orange", "Banana")
            .subscribe(                                          //Onsubscribe
                { value -> Log.e(TAG,"Received: $value")}, // onNext
                { error -> Log.e(TAG,"Error: $error") },    // onError
                { Log.e(TAG,"Completed") }                 // onComplete
            )


        //fromArray

        Observable.fromArray("fromArray", "Orange", "Banana")
            .subscribe { Log.e(TAG, it) }

        Observable.fromIterable(listOf("Apple", "Orange", "Banana"))
            .subscribe(
                { value -> Log.e(TAG,"ReceivedfromIterable: $value") },      // onNext
                { error -> Log.e(TAG,"ErrorIterable: $error")},         // onError
                {  Log.e(TAG,"CompletedIterable") }                       // onComplete
            )



        //Create

        fun getObservableFromList(myList: List<String>) =
            Observable.create<String> { emitter ->
                myList.forEach { kind ->
                    if (kind == "") {
                        emitter.onError(Exception("There's no value to show"))
                    }
                    emitter.onNext(kind)
                }
                emitter.onComplete()
            }

        getObservableFromList(listOf("Blue", "Red", "Black"))
            .subscribe { Log.e(TAG," Received: $it") }



        //Error
        getObservableFromList(listOf("Apple", "", "Banana"))
            .subscribe(
                { value -> Log.e(TAG," Received: $value")  },
                { error -> Log.e(TAG," Error: $error") }
            )




        //Range
        fun printEven(value: Int) {
            if (value % 2 == 0) {
                Log.e(TAG,"$value")
            }
        }
        Observable.range(0, 9).subscribe(::printEven)





        /**
        Emitter Types
         */

        Flowable.just("This is a Flowable")
            .subscribe(//Onsubscribe
                { value -> Log.e(TAG1,"Received: $value")}, // onNext
                { error -> Log.e(TAG1,"Error: $error") },    // onError
                { Log.e(TAG1,"Completed") }                 // onComplete
            )


        Maybe.just("This is a Maybe")
            .subscribe(//Onsubscribe
                { value -> Log.e(TAG1,"Received: $value")}, // onSuccess
                { error -> Log.e(TAG1,"Error: $error") },    // onError
                { Log.e(TAG1,"Completed") }                 // onComplete
            )





        Single.just("This is a Single")
            .subscribe( //OnSubscribe
                { v -> Log.e(TAG1," Received: $v")  },//OnSuccess
                { error -> Log.e(TAG1," Error: $error") }//OnError
            )


        Completable.create { emitter ->
            emitter.onComplete()
            emitter.onError(Exception())
        }



        //Using Flowable to prevent Backpressure
        val observable = PublishSubject.create<Int>()
        observable
            .toFlowable(BackpressureStrategy.DROP)
            .observeOn(Schedulers.computation())
            .subscribe (
                {
                    Log.e(TAG1,"Number is $it")
                },
                {t->
                    print(t.message)
                }
            )
        for (i in 0..1000000){
            observable.onNext(i)
        }


        //Schedulers
        Observable.just("Apple", "Orange", "Banana")
            .subscribeOn(Schedulers.io())//observable emits values on IO
            .observeOn(AndroidSchedulers.mainThread()) //Values will be observed on mainThread
            .subscribe{ v -> Log.e(TAG1,"Received: $v") }






       //Transforming operator


        Observable.just("Water", "Fire", "Wood")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { m -> m + " 2" }
            .subscribe { v -> Log.e(TAG2,"ReceivedMap: $v") }



        Observable.just("Water", "Fire", "Wood")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { m ->
                Observable.just(m + " 2")
                    .subscribeOn(Schedulers.io())
            }
            .subscribe { v -> Log.e(TAG2,"ReceivedFlatMap: $v") }





        val test1 = Observable.just("Apple", "Orange", "Banana")
        val test2 = Observable.just("Microsoft", "Google")
        val test3 = Observable.just("Grass", "Tree", "Flower", "Sunflower")

        Observable.concat(test1, test2, test3)
            .subscribe{ x -> Log.e(TAG2,"Concat: $x") }





        /**
        Filtering Operators
         */


        Observable.just(2,2, 30, 22, 5,5, 60, 1)
            .filter{ x -> x < 10 }
            .distinct()
            .subscribe{ x -> Log.e(TAG3,"Filtered: $x") }


        Observable.just(2, 30, 22, 5, 60, 1)
            .skip(2)
            .skipLast(2)
            .subscribe{ x -> Log.e(TAG3,"Received: $x") }



        Observable.just("Reactive", "X")
            .repeat(2)
            .subscribe { value ->  Log.e(TAG3,"Repeated: $value") }


        Observable.just("Apple", "Orange", "Banana")
            .take(2)
            .subscribe { v -> Log.e(TAG3,"Took: $v")}



        val compositeDisposable = CompositeDisposable()

        val observableOne = Observable.just("Tree")
            .subscribe { v -> Log.e(TAG3,"Received: $v") }
        val observableTwo = Observable.just("Blue")
            .subscribe { v ->  Log.e(TAG3,"Received: $v") }

        compositeDisposable.add(observableOne)
        compositeDisposable.add(observableTwo)
        compositeDisposable.clear()

    }

}