<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="start" role="graph" edgeids="false" edgemode="directed">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1234"/>
        <node id="n1235"/>
        <node id="n1236"/>
        <node id="n1237"/>
        <node id="n1238"/>
        <node id="n1239"/>
        <edge from="n1236" to="n1237">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge from="n1238" to="n1238">
            <attr name="label">
                <string>Train</string>
            </attr>
        </edge>
        <edge from="n1235" to="n1237">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge from="n1236" to="n1236">
            <attr name="label">
                <string>Person</string>
            </attr>
        </edge>
        <edge from="n1239" to="n1234">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n1234" to="n1234">
            <attr name="label">
                <string>Station</string>
            </attr>
        </edge>
        <edge from="n1236" to="n1239">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge from="n1238" to="n1239">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge from="n1237" to="n1237">
            <attr name="label">
                <string>Station</string>
            </attr>
        </edge>
        <edge from="n1239" to="n1239">
            <attr name="label">
                <string>Station</string>
            </attr>
        </edge>
        <edge from="n1235" to="n1235">
            <attr name="label">
                <string>Person</string>
            </attr>
        </edge>
        <edge from="n1234" to="n1237">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n1237" to="n1239">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n1235" to="n1234">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
    </graph>
</gxl>
