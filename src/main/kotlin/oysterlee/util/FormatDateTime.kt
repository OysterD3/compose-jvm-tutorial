package oysterlee.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

//all data time converter
fun formatDateTime(datetime: Instant): String {
  val date = datetime.toLocalDateTime(TimeZone.currentSystemDefault())
  val month = date.monthNumber.toString().padStart(2, '0')
  val day = date.dayOfMonth.toString().padStart(2, '0')
  val hour = date.hour.toString().padStart(2, '0')
  val minute = date.minute.toString().padStart(2, '0')
  val second = date.second.toString().padStart(2, '0')
  return "${date.year}-$month-$day $hour:$minute:$second"
}
