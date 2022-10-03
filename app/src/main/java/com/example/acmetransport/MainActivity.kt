package com.example.acmetransport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.acmetransport.data.CustomAdapter
import com.example.acmetransport.data.ItemCardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    val patternVowel = Regex("^[aeiouAEIOU]+$")
    val patternConsonat = Regex("[b-df-hj-np-tv-zB-DF-HJ-NP-TV-Z]+$")
    var arrayShipment:Array<String> = arrayOf()
    var arrayDrivers:Array<String> = arrayOf()
    var mutableListDrivers:MutableList<String> = mutableListOf()
    val mutableListShipmentEven:MutableList<String> = mutableListOf()
    val mutableListShipmentOdd:MutableList<String> = mutableListOf()
    val mutableListShipmentRare:MutableList<String> = mutableListOf()
    val mutableListDriversNumberVowels:MutableList<Float> = mutableListOf()
    val mutableListDriversNumberConsonants:MutableList<Int> = mutableListOf()
    val mutableListComplete:MutableList<String> = mutableListOf()
    private var countVowels = 0
    private var countConsonants = 0
    private var sumVowels= 0.0F
    private var sumConsonants = 0
    private var countTotal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val gson = Gson()
        var json : String? = null
        try {
            val  inputStream: InputStream = assets.open("data.json")
            json = inputStream.bufferedReader().use{it.readText()}
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            val tutorialMap: Map<String, Any> = gson.fromJson(json, mapType)
            tutorialMap.forEach { println(it) }
            arrayShipment = tutorialMap.getValue("shipments").toString().removePrefix("[").removeSuffix("]").split(",").toTypedArray()
            arrayDrivers = tutorialMap.getValue("drivers").toString().removePrefix("[").removeSuffix("]").split(",").toTypedArray()
            mutableListDrivers = arrayDrivers.toMutableList()
        } catch (ex: Exception) {
            ex.printStackTrace()

        }
        logic()
        recycler()


    }
    fun logic(){
        for(i in arrayShipment.indices){

            val count= arrayShipment[i].length

            when{
                count % 2 ==0 -> mutableListShipmentEven.add(arrayShipment[i])
                count % 3 ==0 -> mutableListShipmentOdd.add(arrayShipment[i])
                else -> mutableListShipmentRare.add(arrayShipment[i])
            }
        }

        for(i in arrayDrivers.indices){

            for (j in 0 until arrayDrivers[i].length){

                when{
                    patternVowel.matches(arrayDrivers[i][j].toString()) -> countVowels += 1
                    patternConsonat.matches(arrayDrivers[i][j].toString()) -> countConsonants += 1
                }

            }
            mutableListDriversNumberVowels.add(countVowels*1.5F)
            mutableListDriversNumberConsonants.add(countConsonants)
            countVowels = 0
            countConsonants = 0
            sumVowels += mutableListDriversNumberVowels[i]
            sumConsonants += mutableListDriversNumberConsonants[i]
        }

        outer@while (true) {
            for (i in 0 until mutableListDrivers.size){
                if (mutableListDriversNumberVowels[i]>(sumVowels/arrayDrivers.size) && mutableListShipmentEven.size>0){
                    mutableListComplete.add(countTotal,"Address Shipment:${mutableListShipmentEven[i]} Driver:${mutableListDrivers[i]}")
                    mutableListDrivers.removeAt(i)
                    mutableListShipmentEven.removeAt(i)
                    countTotal++
                    Log.i("InfoString", mutableListComplete.toString())
                    continue@outer
                }
                else if (mutableListDriversNumberConsonants[i]>(sumConsonants/arrayDrivers.size) && mutableListShipmentOdd.size>0){
                    mutableListComplete.add(countTotal,"Address Shipment:${mutableListShipmentOdd[mutableListShipmentOdd.size-1]} Driver:${mutableListDrivers[i]}")
                    mutableListDrivers.removeAt(i)
                    mutableListShipmentOdd.removeAt(mutableListShipmentOdd.size-1)
                    countTotal++
                    Log.i("InfoString", mutableListComplete.toString())
                    continue@outer
                }
                else if (mutableListShipmentRare.size>0){
                    mutableListComplete.add(countTotal,"Address Shipment:${mutableListShipmentRare[mutableListShipmentRare.size-1]} Driver:${mutableListDrivers[i]}")
                    mutableListDrivers.removeAt(i)
                    mutableListShipmentRare.removeAt(mutableListShipmentRare.size-1)
                    countTotal++
                    Log.i("InfoString", mutableListComplete.toString())
                    continue@outer
                }
            }
            break
        }
    }
    fun recycler(){
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<ItemCardView>()
        for (i in 0 until mutableListComplete.size) {
            data.add(ItemCardView(R.drawable.id_card, mutableListComplete[i]))
        }
        val adapter = CustomAdapter(data)
        recyclerview.adapter = adapter
    }
}