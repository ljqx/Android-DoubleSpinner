import argparse
parser = argparse.ArgumentParser()
parser.add_argument('version', help='release version')
parser.add_argument(
    '--make',
    default='all',
    choices=['all', 'aar', 'jar'],
    help='make what'
)
args = parser.parse_args()
version = args.version
make = args.make
makeaar = False
makejar = False
if make == 'all' or make == 'aar':
    makeaar = True
if make == 'all' or make == 'jar':
    makejar = True

import os
releasedir = os.path.join('release', version)
try:
    os.makedirs(releasedir)
except OSError as exception:
    if exception.errno == os.errno.EEXIST:
        pass
    else:
        raise

import subprocess
if makeaar:
    subprocess.call(['gradlew', 'aR'], shell=True)
    import shutil
    oldaarpath = os.path.join(
        'double-spinner',
        'build',
        'outputs',
        'aar',
        'double-spinner.aar'
    )
    aarpath = os.path.join(
        releasedir,
        'double-spinner-' + version + '.aar'
    )
    shutil.copyfile(oldaarpath, aarpath)

if makejar:
    sourcejarpath = os.path.join(
        releasedir,
        'double-spinner-' + version + '-sources.jar'
    )
    sourcedir = os.path.join(
        'double-spinner',
        'src',
        'main'
    )
    subprocess.call(
        ['jar', 'cvf', sourcejarpath, '-C', sourcedir, 'main', sourcedir],
        shell=True
    )
