from concurrent.futures import ThreadPoolExecutor, as_completed
from multiprocessing import cpu_count
import subprocess
from tqdm import tqdm
import argparse
import regex as re
import sys
from more_itertools import chunked


EXTRACTOR = re.compile(r'^success = [\d.%]+ \((\d+)/\d+\)$')


def main():
  parser = argparse.ArgumentParser(description='Filter fumens with solution finder',
                                   formatter_class=argparse.ArgumentDefaultsHelpFormatter)
  parser.add_argument('-j', '--jar', help='sfinder jar', default='build/libs/solution-finder-0.jar')
  parser.add_argument('-w', '--workers', help='number of workers', type=int, default=cpu_count())
  args = parser.parse_args()

  def process(line, jar):
    line = line.strip()
    if len(line) == 0:
      return ''
    output = subprocess.run(['java', '-jar', jar, 'percent', '-t', line, '-p', '*!', '-fc', '0', '-th', '1'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    if output.returncode != 0:
      return 'ERROR\t' + line
    output = output.stdout.decode('utf-8').strip().split('\n')
    for l in output:
      if l.startswith('success = '):
        return EXTRACTOR.search(l).group(1) + '\t' + line
    return 'ERROR\t' + line


  TOTAL = 5114600
  K = 1000
  with ThreadPoolExecutor(max_workers=args.workers) as executor:
    with tqdm(total=TOTAL, unit='board', unit_scale=True) as pbar:
      for lines in chunked(sys.stdin, K):
        futures = [executor.submit(process, line, args.jar) for line in lines]
        for future in as_completed(futures):
          line = future.result()
          print(line)
          pbar.update(1)


if __name__ == '__main__':
  main()