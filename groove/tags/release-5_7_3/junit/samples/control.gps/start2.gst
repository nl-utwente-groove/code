<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start2">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1616">
            <attr name="layout">
                <string>532 301 38 15</string>
            </attr>
        </node>
        <node id="n1612">
            <attr name="layout">
                <string>404 221 39 15</string>
            </attr>
        </node>
        <node id="n1615">
            <attr name="layout">
                <string>535 210 38 15</string>
            </attr>
        </node>
        <node id="n1614">
            <attr name="layout">
                <string>357 108 39 15</string>
            </attr>
        </node>
        <node id="n1613">
            <attr name="layout">
                <string>532 142 38 15</string>
            </attr>
        </node>
        <node id="n1617">
            <attr name="layout">
                <string>209 256 39 15</string>
            </attr>
        </node>
        <node id="n1618">
            <attr name="layout">
                <string>94 150 28 15</string>
            </attr>
        </node>
        <edge to="n1618" from="n1618">
            <attr name="label">
                <string>Train</string>
            </attr>
        </edge>
        <edge to="n1616" from="n1615">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n1616" from="n1616">
            <attr name="label">
                <string>Station</string>
            </attr>
        </edge>
        <edge to="n1616" from="n1612">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge to="n1613" from="n1613">
            <attr name="label">
                <string>Station</string>
            </attr>
        </edge>
        <edge to="n1615" from="n1614">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge to="n1613" from="n1614">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n1613" from="n1616">
            <attr name="label">
                <string>next</string>
            </attr>
            <attr name="layout">
                <string>500 0 580 306 641 149 580 149 14</string>
            </attr>
        </edge>
        <edge to="n1614" from="n1614">
            <attr name="label">
                <string>Person</string>
            </attr>
        </edge>
        <edge to="n1616" from="n1617">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge to="n1612" from="n1612">
            <attr name="label">
                <string>Person</string>
            </attr>
        </edge>
        <edge to="n1615" from="n1615">
            <attr name="label">
                <string>Station</string>
            </attr>
        </edge>
        <edge to="n1616" from="n1618">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n1617" from="n1617">
            <attr name="label">
                <string>Person</string>
            </attr>
        </edge>
        <edge to="n1613" from="n1617">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n1615" from="n1612">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n1615" from="n1613">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
    </graph>
</gxl>
