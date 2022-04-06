import logging
from datetime import datetime
from typing import Union


def remove_namespace(tag: str):
    """Removes xmlns from tag string.

    --------
    Examples

    >>> remove_namespace("{http://ec.europa.eu/fisheries/schema/ers/v3}OPS")
    "OPS"
    """
    return tag.split("}")[-1]


def get_root_tag(xml_element):
    root_tag = remove_namespace(xml_element.tag)
    return root_tag


def try_float(s: str):
    try:
        return float(s)
    except:
        return None


def get_first_child(xml_element, assert_child_single=True):
    children = list(xml_element)

    if assert_child_single:
        assert len(children) == 1

    return children[0]


def tagged_children(el):
    children = list(el)
    res = {}

    for child in children:
        tag = remove_namespace(child.tag)
        if tag in res:
            res[tag].append(child)
        else:
            res[tag] = [child]

    return res


def make_datetime(date: str, time: Union[str, None] = None):
    """Takes date a "2020-12-24" string and, optionnally, a time "16:49" string,
    Returns a datetime object"""
    datetime_string = date
    datetime_format = "%Y-%m-%d"

    if date:
        if time:
            datetime_string += f" {time}"
            datetime_format += " %H:%M"
        try:
            res = datetime.strptime(datetime_string, datetime_format)
        except ValueError:
            logging.warning("ERS datetime could not be parsed")
            res = None
    else:
        res = None

    return res


def make_datetime_json_serializable(date: str, time: Union[str, None] = None):
    """Returns a serialized (string) datetime object make from a ISO format date string
    and an optional time string.

    Args:
        date (str): ISO format date string. Egg:= '2021-10-25'
        time (Union[str, None], optional): ISO format time string
          Egg '12:00'. Defaults to None.

    Returns:
        [type]: [description]
    """
    dt = make_datetime(date, time)
    if dt:
        return dt.isoformat() + "Z"
    else:
        return None
