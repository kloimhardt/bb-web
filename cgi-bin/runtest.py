import sys
sys.path.append("cgi-bin/hy-0.20.0+129.g9733a3dd-py3.6.egg")
sys.path.append("cgi-bin/transit_python-0.8.284-py3.6.egg")
sys.path.append("cgi-bin/sympy-1.8.dev0-py3.9.egg")

import pytest
pytest.main(args=["--ignore=somedirectory"])
