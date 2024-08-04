from demo.calculate import sum


def test_sum():
    assert sum(1, 1) == 2
    assert sum(2, 1) == 3
    assert sum(1, 2) == 3


def test_sum_negative():
    assert sum(1, -1) == 0
    assert sum(-1, 1) == 0
    assert sum(-1, -1) == -2
