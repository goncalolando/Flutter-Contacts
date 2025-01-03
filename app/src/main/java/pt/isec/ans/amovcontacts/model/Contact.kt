package pt.isec.ans.amovcontacts.model

import java.io.Serializable
import java.util.Date

data class MeetingPoint(
    val latitude: Double,
    val longitude: Double,
    val date: Date
) : Serializable

class Contact(
    var name: String,
    var email: String,
    var phone: String,
    var birthday: Date?=null,
    var picture: String?=null
): Serializable {
    private val history: MutableList<MeetingPoint> = mutableListOf()

    fun addMeetingPoint(latitude: Double, longitude: Double, date: Date) {
        history.add(MeetingPoint(latitude, longitude, date))
    }
    fun addMeetingPoint(meetingPoint: MeetingPoint) {
        history.add(meetingPoint)
    }
    fun getMeetingPoints(): List<MeetingPoint> {
        return history.toList()
    }
}