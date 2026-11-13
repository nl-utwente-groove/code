<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="coeffCells-0-0">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n">
            <attr name="layout">
                <string>80 80 60 30</string>
            </attr>
        </node>
        <node id="c0">
            <attr name="layout">
                <string>310 80 60 30</string>
            </attr>
        </node>
        <node id="c1">
            <attr name="layout">
                <string>310 190 60 30</string>
            </attr>
        </node>
        <node id="c2">
            <attr name="layout">
                <string>310 300 60 30</string>
            </attr>
        </node>
        <edge from="n" to="n">
            <attr name="label">
                <string>let:p=UserTypePoly("p",5,6,5)</string>
            </attr>
        </edge>
        <edge from="c0" to="c0">
            <attr name="label">
                <string>let:val=5</string>
            </attr>
        </edge>
        <edge from="c1" to="c1">
            <attr name="label">
                <string>let:val=6</string>
            </attr>
        </edge>
        <edge from="c2" to="c2">
            <attr name="label">
                <string>let:val=5</string>
            </attr>
        </edge>
    </graph>
</gxl>
