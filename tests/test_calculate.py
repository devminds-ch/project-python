from python_training_project.calculate import calc_sum


def test_calc_sum():
    assert calc_sum(1, 1) == 2
    assert calc_sum(2, 1) == 3
    assert calc_sum(1, 2) == 3


def test_sum_negative():
    assert calc_sum(1, -1) == 0
    assert calc_sum(-1, 1) == 0
    assert calc_sum(-1, -1) == -2
