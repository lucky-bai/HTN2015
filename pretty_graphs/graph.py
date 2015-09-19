import datetime

SLEEP_MIN = datetime.timedelta(hours = 4)
SLEEP_MAX = datetime.timedelta(hours = 11)

# map [canonical date] -> hours slept, float
hours_slept = {}

def main():
  # Parse into datetime
  dfile = open('../export_chrome_db/bai_chrome.txt', 'r').readlines()
  dates = []
  for dt_line in dfile:
    dates.append(datetime.datetime.fromtimestamp(int(dt_line)))

  # find sleeping (large gaps)
  for i in xrange(len(dates)):
    if i == 0: continue
    ts_interval = dates[i] - dates[i-1]
    if ts_interval > SLEEP_MIN and ts_interval < SLEEP_MAX:
      canonical_date = datetime.datetime(dates[i].year, dates[i].month, dates[i].day)
      if canonical_date not in hours_slept:
        hours_slept[canonical_date] = ts_interval.seconds / 3600.0
      else:
        hours_slept[canonical_date] = max(ts_interval.seconds / 3600.0, hours_slept[canonical_date])

  for a,b in hours_slept.iteritems():
    print a,b

main()


