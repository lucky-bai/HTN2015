import datetime
import time

SLEEP_MIN = datetime.timedelta(hours = 4)
SLEEP_MAX = datetime.timedelta(hours = 11)


def compute_sleep_time(dates):
  """
  Input: list of UNIX timestamps
  Output: list of [(date, hours slept)]
  """
  # map [canonical date] -> hours slept, float
  hours_slept = {}
  sleep_begin = {}
  sleep_end = {}

  # find sleeping (large gaps)
  for i in xrange(len(dates)):
    if i == 0: continue
    ts_interval = dates[i] - dates[i-1]

    # assume you will not sleep more than 11 hours or less than 4
    # assume that you wake up between 5am and 2pm
    # If multiple intervals fit this criterion choose the longest
    if ts_interval > SLEEP_MIN and ts_interval < SLEEP_MAX and \
        dates[i].hour <= 14 and dates[i].hour >= 5:
      canonical_date = datetime.datetime(dates[i].year, dates[i].month, dates[i].day)
      if canonical_date not in hours_slept:
        hours_slept[canonical_date] = ts_interval.seconds / 3600.0
        sleep_begin[canonical_date] = dates[i-1]
        sleep_end[canonical_date] = dates[i]
      else:
        hours_slept[canonical_date] = max(ts_interval.seconds / 3600.0, hours_slept[canonical_date])
        sleep_begin[canonical_date] = dates[i-1]
        sleep_end[canonical_date] = dates[i]

  # Convert to list, average, etc
  hours_slept_list = []
  for a,b in hours_slept.iteritems():
    hours_slept_list.append((a,b))
  hours_slept_list = sorted(hours_slept_list)

  # debug
  sm = 0
  for x in hours_slept_list:
    print x[0], x[1], sleep_begin[x[0]], sleep_end[x[0]], time.mktime(sleep_begin[x[0]].timetuple()), time.mktime(sleep_end[x[0]].timetuple())
    sm += x[1]

  print "avg", sm / len(hours_slept)
  print "num", len(hours_slept)

  return hours_slept_list


def main():
  # Parse into datetime
  dfile = open('../export_chrome_db/bai_chrome.txt', 'r').readlines()
  dates = []
  for dt_line in dfile:
    dates.append(datetime.datetime.fromtimestamp(int(dt_line)))

  hours_slept_list = compute_sleep_time(dates)
  print len(dates)

main()


