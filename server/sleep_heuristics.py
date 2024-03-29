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
  dates = sorted(dates)
  dates = [datetime.datetime.fromtimestamp(date) for date in dates]

  # find sleeping (large gaps)
  for i in xrange(len(dates)):
    if i == 0: continue
    ts_interval = dates[i] - dates[i-1]

    # assume you will not sleep more than 11 hours or less than 4
    # assume that you wake up between 5am and 2pm
    # If multiple intervals fit this criterion choose the longest
    if ts_interval > SLEEP_MIN and ts_interval < SLEEP_MAX and \
        dates[i].hour <= 14+5 and dates[i].hour >= 5+5:
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
    hours_slept_list.append((time.mktime(a.timetuple()),b))
  hours_slept_list = sorted(hours_slept_list)

  return hours_slept_list
