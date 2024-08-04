import click
import logging
import sys

from demo.calculate import sum
from demo.version import version

log = logging.getLogger(__name__)


@click.group()
@click.version_option(version)
def cli():
    logging.basicConfig(
        stream=sys.stdout,
        level=logging.INFO,
        format='%(asctime)s %(name)-16s %(levelname)-8s %(message)s')


@cli.command(name='sum')
@click.argument('a', type=float)
@click.argument('b', type=float)
def cli_sum(a: float, b: float):
    log.info(f'Sum of {a} and {b} is {sum(a, b)}')


if __name__ == '__main__':
    cli()
